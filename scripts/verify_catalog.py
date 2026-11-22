"""Local/test DB integration checks for catalogue and maintenance CRUD.
Requires requests, a MySQL CLI, seeded accounts, and a running application.
CARCARE_{ADMIN,MANAGER,RECEPTIONIST,CUSTOMER,TECHNICIAN}_PASSWORD: app passwords.
CARCARE_TEST_DB: explicit disposable database name. CARCARE_MYSQL: MySQL CLI path.
CARCARE_DB_USER (default root); MYSQL_PWD supplies the DB password if needed.
Creates and cleans only records belonging to this test run, including audit fixtures.
"""
import os
import re
import subprocess
import uuid
from datetime import datetime, timedelta
from concurrent.futures import ThreadPoolExecutor
import requests

BASE = os.environ.get('CARCARE_URL', 'http://localhost:8080/carcare')
DB = os.environ.get('CARCARE_TEST_DB', '')
if not re.fullmatch(r'[A-Za-z0-9_]+', DB):
    raise SystemExit('Set CARCARE_TEST_DB to a local/test database first.')
for user in ['ADMIN','MANAGER','RECEPTIONIST','CUSTOMER','TECHNICIAN']:
    if not os.environ.get('CARCARE_' + user + '_PASSWORD'):
        raise SystemExit('Set CARCARE_' + user + '_PASSWORD first.')
count = 0

def check(condition, label):
    global count
    assert condition, label
    count += 1
    print('PASS:', label)

def sql(statement):
    result = subprocess.run([os.environ.get('CARCARE_MYSQL', 'mysql'), '-u',
        os.environ.get('CARCARE_DB_USER','root'), '--default-character-set=utf8mb4',
        '--batch', '--skip-column-names', DB, '-e', statement], capture_output=True, text=True, encoding='utf-8')
    if result.returncode:
        raise RuntimeError(result.stderr)
    return result.stdout.strip()

def login(user):
    session = requests.Session()
    response = session.post(BASE+'/auth',data={'username':user,'password':os.environ['CARCARE_'+user.upper()+'_PASSWORD']})
    assert response.url.endswith('/home'), 'Login failed: '+user
    return session

s = login('admin')
html = s.get(BASE+'/services').text
token = re.search(r'name="csrf" value="([^"]+)"',html).group(1)

def post(path, **data):
    return s.post(BASE+path,data={'csrf':token,**data},allow_redirects=False)

suffix = uuid.uuid4().hex[:10]
service_name = 'Integration Service '+suffix
bay_name = 'Integration Bay '+suffix
service_ids = []
bay_id = booking_id = None
maint_ids = []
try:
    check(s.get(BASE+'/services').status_code == 200, 'Load service list and forms')
    check(s.get(BASE+'/bays').status_code == 200, 'Load bay and maintenance forms')
    check(s.post(BASE+'/services',data={'action':'service'}).status_code == 403, 'Reject POST without CSRF')
    service = dict(action='service',service_name=service_name,description='Safe <script>alert(1)</script>',base_price='150000',estimated_duration_minutes='45',status='ACTIVE')
    check(post('/services',**service).status_code == 302, 'Create service with price and duration')
    sid = sql("SELECT service_id FROM services WHERE service_name='"+service_name+"'")
    service_ids.append(sid)
    check(sql('SELECT base_price,estimated_duration_minutes FROM services WHERE service_id='+sid) == '150000.00\t45', 'Persist exact price and duration')
    check('&lt;script&gt;alert(1)&lt;/script&gt;' in s.get(BASE+'/services').text, 'Escape description markup')
    check(post('/services',**service).status_code == 400, 'Reject duplicate service name')
    for field,value in [('base_price','-1'),('base_price','1.001'),('base_price','10000000000'),('estimated_duration_minutes','0'),('estimated_duration_minutes','1.5'),('estimated_duration_minutes','1441'),('service_name',' '),('status','INVALID')]:
        check(post('/services',**{**service,'id':sid,field:value}).status_code == 400, 'Reject invalid '+field+'='+value)
    check(sql('SELECT COUNT(*) FROM service_price_history WHERE service_id='+sid) == '0', 'Invalid updates do not create audit entries')
    bay = dict(action='bay',bay_name=bay_name,bay_type='WASHING',status='AVAILABLE')
    check(post('/bays',**bay).status_code == 302, 'Create bay')
    bay_id = sql("SELECT bay_id FROM service_bays WHERE bay_name='"+bay_name+"'")
    check(post('/bays',**bay).status_code == 400, 'Reject duplicate bay name')
    check(post('/bays',**{**bay,'id':bay_id,'bay_type':'DETAILING'}).status_code == 302, 'Update bay type')
    check(sql('SELECT bay_type FROM service_bays WHERE bay_id='+bay_id) == 'DETAILING', 'Read updated bay type')
    check(post('/bays',**{**bay,'id':bay_id,'status':'INVALID'}).status_code == 400, 'Reject invalid bay status')
    # Reference fixtures prove price changes do not rewrite booked prices and maintenance cannot overlap assignments.
    booking_id = sql("INSERT INTO bookings(booking_code,customer_id,vehicle_id,booking_time,status) VALUES('IT"+suffix+"',1,1,'2097-01-01 08:00:00','CONFIRMED'); SELECT LAST_INSERT_ID();")
    sql('INSERT INTO booking_services(booking_id,service_id,price) VALUES('+booking_id+','+sid+',150000)')
    sql("INSERT INTO assignments(booking_id,bay_id,technician_id,start_time,estimated_end_time,status) VALUES("+booking_id+','+bay_id+",1,'2097-01-01 08:00:00','2097-01-01 10:00:00','ASSIGNED')")
    updated = {**service,'id':sid,'base_price':'175000.50','estimated_duration_minutes':'60'}
    check(post('/services',**updated).status_code == 302, 'Update service price and duration')
    check(sql('SELECT old_price,new_price,changed_by FROM service_price_history WHERE service_id='+sid) == '150000.00\t175000.50\t1', 'Audit old/new price and actor in same transaction')
    check(sql('SELECT price FROM booking_services WHERE booking_id='+booking_id) == '150000.00', 'Preserve booked price')
    check(s.get(BASE+'/services?edit='+sid).status_code == 200, 'Load edit form with price history')
    check(post('/services',**updated).status_code == 302, 'Save unchanged price')
    check(sql('SELECT COUNT(*) FROM service_price_history WHERE service_id='+sid) == '1', 'Unchanged price creates no duplicate audit')
    check(post('/services',action='deleteService',id=sid).status_code == 400, 'Protect used service and price history from deletion')
    check(post('/services',**{**updated,'status':'INACTIVE'}).status_code == 302, 'Deactivate used service')
    fresh = {**service,'service_name':service_name+' unused'}
    check(post('/services',**fresh).status_code == 302, 'Create unused service for delete')
    fresh_id = sql("SELECT service_id FROM services WHERE service_name='"+fresh['service_name']+"'");service_ids.append(fresh_id)
    check(post('/services',action='deleteService',id=fresh_id).status_code == 302, 'Delete unused service')
    check(sql('SELECT COUNT(*) FROM services WHERE service_id='+fresh_id) == '0', 'Deleted service no longer exists')
    m = dict(action='maintenance',bay_id=bay_id,start_time='2097-01-01T09:00',end_time='2097-01-01T11:00',reason='Integration maintenance',status='SCHEDULED')
    check(post('/bays',**m).status_code == 400, 'Reject maintenance overlapping assigned work')
    m.update(start_time='2097-01-02T08:00',end_time='2097-01-02T10:00')
    check(post('/bays',**m).status_code == 302, 'Create maintenance schedule')
    mid = sql('SELECT maintenance_id FROM bay_maintenance_schedules WHERE bay_id='+bay_id);maint_ids.append(mid)
    check(post('/bays',**m).status_code == 400, 'Reject overlapping maintenance')
    check(post('/bays',**{**m,'id':mid,'reason':'Updated maintenance'}).status_code == 302, 'Update maintenance without self-conflict')
    edit_page = s.get(BASE+'/bays?maintenanceEdit='+mid)
    check(edit_page.status_code == 200 and 'Updated maintenance' in edit_page.text, 'Read maintenance edit form with datetime')
    for changes in [dict(end_time='2097-01-02T07:00'),dict(start_time='bad'),dict(bay_id='999999999'),dict(reason=' '),dict(status='INVALID')]:
        check(post('/bays',**{**m,**changes}).status_code == 400, 'Reject invalid maintenance '+str(changes))
    check(post('/bays',action='deleteBay',id=bay_id).status_code == 400, 'Protect bay with assignments and maintenance')
    def concurrent(_):
        worker = requests.Session();worker.cookies.update(s.cookies)
        return worker.post(BASE+'/bays',data={'csrf':token,**{**m,'start_time':'2097-01-02T10:00','end_time':'2097-01-02T12:00'}},allow_redirects=False).status_code
    with ThreadPoolExecutor(max_workers=2) as pool:
        check(sorted(pool.map(concurrent,range(2))) == [302,400], 'Concurrent duplicate schedules allow exactly one; adjacent schedule allowed')
    maint_ids = sql('SELECT maintenance_id FROM bay_maintenance_schedules WHERE bay_id='+bay_id).splitlines()
    check(post('/bays',**{**m,'id':mid,'status':'CANCELLED'}).status_code == 302, 'Cancel maintenance')
    check(post('/bays',**m).status_code == 302, 'Cancelled window becomes available')
    maint_ids = sql('SELECT maintenance_id FROM bay_maintenance_schedules WHERE bay_id='+bay_id).splitlines()
    for item in maint_ids:
        check(post('/bays',action='deleteMaintenance',id=item).status_code == 302, 'Delete maintenance '+item)
    maint_ids.clear()
    # A current schedule automatically changes effective bay availability, then completion releases it.
    now = datetime.now()
    current = {**m,'start_time':(now-timedelta(hours=1)).isoformat(timespec='minutes'),'end_time':(now+timedelta(hours=1)).isoformat(timespec='minutes'),'status':'IN_PROGRESS'}
    check(post('/bays',**current).status_code == 302, 'Start current maintenance')
    mid = sql('SELECT maintenance_id FROM bay_maintenance_schedules WHERE bay_id='+bay_id);maint_ids.append(mid)
    row = re.search(r'<tr data-record-id="'+bay_id+r'">(.*?)</tr>',s.get(BASE+'/bays').text,re.S).group(1)
    check('Theo lịch bảo trì' in row, 'Current maintenance changes displayed bay status')
    check(post('/bays',**{**bay,'id':bay_id,'status':'OCCUPIED'}).status_code == 400, 'Cannot occupy a bay currently under maintenance')
    check(post('/bays',**{**current,'id':mid,'status':'COMPLETED'}).status_code == 302, 'Complete maintenance')
    row = re.search(r'<tr data-record-id="'+bay_id+r'">(.*?)</tr>',s.get(BASE+'/bays').text,re.S).group(1)
    check('Theo lịch bảo trì' not in row and 'Trống' in row, 'Completion releases bay to base status')
    for user in ['customer','technician','receptionist']:
        other = login(user)
        check(other.post(BASE+'/services',data={'action':'deleteService','id':sid,'csrf':token}).status_code == 403, user+' cannot mutate services')
        check(other.get(BASE+'/bays').status_code == 403, user+' cannot view bay administration')
    receptionist = login('receptionist')
    response = receptionist.get(BASE+'/services')
    check(response.status_code == 200 and 'id="serviceForm"' not in response.text, 'Receptionist sees price list without management form')
    check(receptionist.get(BASE+'/services?edit='+sid).status_code == 403, 'Receptionist cannot load edit form')
    manager = login('manager')
    check(manager.get(BASE+'/services').status_code == 200 and manager.get(BASE+'/bays').status_code == 200, 'Manager can access both modules')
    check(requests.get(BASE+'/bays',allow_redirects=False).status_code == 302, 'Anonymous user must log in')
    check(post('/bays',action='deleteMaintenance',id='999999999').status_code == 400, 'Missing record gets validation error')
finally:
    # Cleanup is deliberately limited to IDs created by this run on the explicit test database.
    if bay_id:
        sql('DELETE FROM bay_maintenance_schedules WHERE bay_id='+bay_id)
    if booking_id:
        sql('DELETE FROM assignments WHERE booking_id='+booking_id)
        sql('DELETE FROM booking_services WHERE booking_id='+booking_id)
        sql('DELETE FROM bookings WHERE booking_id='+booking_id)
    if bay_id:
        check(post('/bays',action='deleteBay',id=bay_id).status_code == 302,'Delete unused bay after removing test references')
    for sid in service_ids:
        sql('DELETE FROM service_price_history WHERE service_id='+sid)
        sql('DELETE FROM services WHERE service_id='+sid)
print(f'All {count} catalogue integration checks passed.')
