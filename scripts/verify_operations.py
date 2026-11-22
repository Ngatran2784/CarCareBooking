"""Integration checks against a running app with the repository's seed accounts.
Run only on a local/test database: python scripts/verify_operations.py
Requires requests. Creates and removes its own records; restores workshop settings.
"""
import os
import re
from datetime import date, timedelta
from concurrent.futures import ThreadPoolExecutor
import requests

BASE = os.environ.get('CARCARE_URL', 'http://localhost:8080/carcare')
checks = 0
for account in ['ADMIN', 'MANAGER', 'CUSTOMER', 'TECHNICIAN', 'RECEPTIONIST']:
    if not os.environ.get('CARCARE_' + account + '_PASSWORD'):
        raise SystemExit('Set CARCARE_' + account + '_PASSWORD for the local test account before running.')

def check(condition, message):
    global checks
    assert condition, message
    checks += 1
    print('PASS:', message)

def login(user):
    password = os.environ['CARCARE_' + user.upper() + '_PASSWORD']
    session = requests.Session()
    response = session.post(BASE + '/auth', data={'username': user, 'password': password})
    assert response.url.endswith('/home'), response.url
    return session

s = login('admin')
page = s.get(BASE + '/staff')
check(page.status_code == 200, 'Admin can load technician management')
token = re.search(r'name="csrf" value="([^"]+)"', page.text).group(1)
tech_options = re.search(r'id="shiftstaff".*?</select>', page.text, re.S).group(0)
service_options = re.search(r'id="skillservice".*?</select>', page.text, re.S).group(0)
existing = set(re.findall(r'edit-skill" data-id="\d+" data-staff="(\d+)" data-service="(\d+)"', page.text))
pairs = [(t, v) for t in re.findall(r'<option value="(\d+)">', tech_options)
         for v in re.findall(r'<option value="(\d+)">', service_options) if (t,v) not in existing]
assert pairs, 'Use a test DB with at least one unassigned technician/service pair.'
tech, service = pairs[0]

def post(path, **data):
    return s.post(BASE + path, data={'csrf': token, **data}, allow_redirects=False)

def row_id(kind):
    html = s.get(BASE + '/staff').text
    suffix = (r' data-staff="' + tech + r'" data-service="' + service + r'"') if kind == 'skill' else (r' data-staff="' + tech + r'" data-date="' + work_date + r'"')
    return re.search(r'class="[^"]*edit-' + kind + r'" data-id="(\d+)"' + suffix, html).group(1)

cfg_page = s.get(BASE + '/settings').text
original = {key: re.search(r'name="' + key + r'"[^>]*value="([^"]+)"', cfg_page).group(1)
            for key in ['opening', 'closing', 'buffer', 'interval']}
shift_ids = []
skill_id = None
holiday_id = None
# Use dates with no existing shifts or holidays; never modify pre-existing personnel records.
offset = 90
while True:
    work_date = (date.today() + timedelta(days=offset)).isoformat()
    holiday_date = (date.today() + timedelta(days=offset+1)).isoformat()
    if all(d not in page.text and d not in cfg_page for d in [work_date,holiday_date]):
        break
    offset += 2
try:
    check(post('/staff', action='shift').status_code == 400, 'Missing fields return validation error')
    check(s.post(BASE + '/settings', data={'action': 'hours'}).status_code == 403, 'Missing CSRF token is rejected')
    for values in [dict(opening='18:30', closing='07:30', buffer='15', interval='30'),
                   dict(opening='07:30', closing='18:30', buffer='-1', interval='30'),
                   dict(opening='07:30', closing='18:30', buffer='15', interval='0')]:
        check(post('/settings', action='hours', **values).status_code == 400, 'Invalid operating configuration is rejected')
    check(post('/settings', action='hours', opening='07:30', closing='18:30', buffer='20', interval='30').status_code == 302, 'Hours and buffer persist successfully')
    check('value="20"' in s.get(BASE + '/settings').text, 'Saved buffer is read back from database')
    skill = dict(action='skill', staffId=tech, serviceId=service, level='ADVANCED')
    check(post('/staff', **skill).status_code == 302, 'Create technician skill')
    skill_id = row_id('skill')
    check(post('/staff', **skill).status_code == 400, 'Duplicate technician/service pair is rejected')
    check(post('/staff', **{**skill, 'id': skill_id, 'level': 'EXPERT'}).status_code == 302, 'Update skill proficiency')
    check(post('/staff', **{**skill, 'id': skill_id, 'level': 'INVALID'}).status_code == 400, 'Unknown proficiency is rejected')
    shift = dict(action='shift', staffId=tech, date=work_date, start='08:00', end='12:00', status='SCHEDULED')
    check(post('/staff', **shift).status_code == 302, 'Create work shift')
    shift_ids.append(row_id('shift'))
    check(post('/staff', **shift).status_code == 400, 'Overlapping shift is rejected')
    check(post('/staff', **{**shift, 'id': shift_ids[0], 'end': '11:30', 'status': 'ATTENDED'}).status_code == 302, 'Update shift without self-overlap')
    check(post('/staff', **{**shift, 'start': '06:00', 'end': '07:00'}).status_code == 400, 'Shift outside opening hours is rejected')
    check(post('/staff', **{**shift, 'start': '13:00', 'end': '12:00'}).status_code == 400, 'Reversed shift times are rejected')
    check(post('/settings', action='holiday', date=work_date, name='Integration holiday').status_code == 400, 'Holiday cannot close an assigned workday')
    check(post('/settings', action='hours', opening='09:00', closing='18:00', buffer='15', interval='30').status_code == 400, 'Hours cannot invalidate existing future shifts')
    # Two simultaneous requests must not create duplicate shifts.
    def concurrent_shift(_):
        worker = requests.Session()
        worker.cookies.update(s.cookies)
        return worker.post(BASE + '/staff', data={'csrf':token, **{**shift,'start':'12:00','end':'16:00'}}, allow_redirects=False).status_code
    with ThreadPoolExecutor(max_workers=2) as pool:
        statuses = sorted(pool.map(concurrent_shift, range(2)))
    check(statuses == [302, 400], 'Concurrent overlapping requests allow only one shift')
    html = s.get(BASE + '/staff').text
    shift_ids = re.findall(r'class="[^"]*edit-shift" data-id="(\d+)"[^>]*data-date="' + work_date + '"', html)
    check(len(shift_ids) == 2, 'Both non-overlapping shifts persist')
    check(post('/settings', action='holiday', date=holiday_date, name='Integration holiday').status_code == 302, 'Create holiday')
    holiday_html = s.get(BASE + '/settings').text
    holiday_id = re.search(r'<tr><td>' + holiday_date + r'</td>.*?name="id" value="(\d+)"', holiday_html, re.S).group(1)
    check(post('/settings', action='holiday', date=holiday_date, name='Updated holiday').status_code == 302, 'Update holiday reason by date')
    check(post('/staff', **{**shift,'date':holiday_date}).status_code == 400, 'No work shift on a closed holiday')
    for user in ['customer', 'technician', 'receptionist']:
        denied = login(user)
        for path in ['/staff','/settings']:
            check(denied.get(BASE+path).status_code == 403, user+' cannot read '+path)
            check(denied.post(BASE+path,data={'action':'deleteShift','id':'1'}).status_code == 403, user+' cannot modify '+path)
    manager = login('manager')
    check(manager.get(BASE+'/staff').status_code == 200 and manager.get(BASE+'/settings').status_code == 200, 'Manager can access both modules')
    anonymous = requests.get(BASE+'/settings',allow_redirects=False)
    check(anonymous.status_code == 302 and '/auth' in anonymous.headers['Location'], 'Anonymous user must log in')
finally:
    for item in shift_ids:
        check(post('/staff',action='deleteShift',id=item).status_code == 302,'Delete test shift '+item)
    if skill_id:
        check(post('/staff',action='deleteSkill',id=skill_id).status_code == 302,'Delete test skill')
    if holiday_id:
        check(post('/settings',action='deleteHoliday',id=holiday_id).status_code == 302,'Delete test holiday')
    check(post('/settings',action='hours',**original).status_code == 302,'Restore original operating settings')
print(f'All {checks} integration checks passed.')
