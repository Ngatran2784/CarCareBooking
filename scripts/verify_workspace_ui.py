from pathlib import Path
import os
from playwright.sync_api import sync_playwright,expect
out=Path(os.environ.get('CARCARE_EVIDENCE_DIR','tmp/workspace-evidence'));out.mkdir(parents=True,exist_ok=True)
password=os.environ['CARCARE_ADMIN_PASSWORD'];base=os.environ.get('CARCARE_URL','http://localhost:8080/carcare')
with sync_playwright() as p:
 b=p.chromium.launch(executable_path=os.environ.get('CARCARE_CHROME','C:/Program Files/Google/Chrome/Application/chrome.exe'),headless=True)
 page=b.new_page(viewport={'width':1600,'height':1050},locale='vi-VN');errors=[];page.on('pageerror',lambda e:errors.append(str(e)))
 page.goto(base+'/auth');page.locator('[name=username]').fill('admin');page.locator('[name=password]').fill(password);page.locator('button[type=submit]').click();page.wait_for_url('**/home')
 for route,name in [('/services','01-danh-muc'),('/staff','02-nhan-su'),('/bookings?action=create','03-chon-xe'),('/vehicles','04-quan-ly-xe'),('/bays','05-khoang')]:
  r=page.goto(base+route);assert r.status==200,(route,r.status,page.locator('body').inner_text()[:1600]);page.wait_for_load_state('networkidle');page.screenshot(path=str(out/(name+'.png')),full_page=True);print(route,'OK')
 page.goto(base+'/services');page.locator('#serviceSearch').fill('zzzz-no-result');expect(page.get_by_role('status')).to_contain_text('Không tìm thấy');page.locator('#serviceSearch').fill('');page.locator('#serviceStatusFilter').select_option('INACTIVE');assert page.locator('#servicesTable tr[data-status=ACTIVE]:visible').count()==0
 page.locator('[data-open-editor=serviceEditorPanel]').click();expect(page.locator('#serviceEditorPanel')).to_have_attribute('open','');page.screenshot(path=str(out/'06-them-dich-vu.png'),full_page=True)
 page.goto(base+'/staff');page.locator('[data-open-editor=staffEditorPanel]').click();expect(page.locator('#staffEditorPanel')).to_have_attribute('open','');page.screenshot(path=str(out/'07-form-nhan-su.png'),full_page=True)
 edit=page.locator('.edit-shift');
 if edit.count():
  edit.first.click();assert page.locator('#shiftForm [name=id]').input_value()!='0';page.locator('#shiftForm button[type=reset]').click();assert page.locator('#shiftForm [name=id]').input_value()=='0'
 page.goto(base+'/bookings?action=create');expect(page.locator('#confirmVehicle')).to_be_disabled();radios=page.locator('[name=vehicleId]');assert radios.count()>0
 radios.first.check();plate=radios.first.get_attribute('data-plate');expect(page.locator('#selectedVehiclePlate')).to_have_text(plate);page.locator('#confirmVehicle').click();expect(page.get_by_role('status')).to_contain_text(plate);page.screenshot(path=str(out/'08-xac-nhan-xe.png'),full_page=True)
 page.locator('#vehicleSearch').fill('zzzz-no-result');expect(page.locator('#vehicleNoResults')).to_be_visible();page.locator('#vehicleSearch').fill('')
 for route,name in [('/services','09-danh-muc-mobile'),('/staff','10-nhan-su-mobile'),('/bookings?action=create','11-chon-xe-mobile')]:
  page.set_viewport_size({'width':390,'height':844});page.goto(base+route);page.wait_for_load_state('networkidle');assert page.evaluate('document.documentElement.scrollWidth <= innerWidth'),route;page.screenshot(path=str(out/(name+'.png')),full_page=True)
 assert not errors,errors
 print('Browser checks passed; 11 screenshots saved. No JavaScript errors.')
 b.close()
