-- =============================================================================
-- BẢN SAO LƯU DỮ LIỆU BỊ LỖI MÃ HÓA (MOJIBAKE) TRƯỚC KHI KHẮC PHỤC
-- Thời gian sao lưu: 2026-11-10 23:28:00
-- Dự án: CarCareBooking (carcare_db)
-- Mục đích: Lưu trữ đối chiếu trạng thái lỗi bảng users, service_bays, roles,
--           customers, employees, services, staff trước khi UPDATE dữ liệu chuẩn.
-- =============================================================================

-- 1. Bảng users (trước khi sửa):
-- user_id 1: 'Quß║ún Trß╗ï Vi├¬n Hß╗ç Thß╗æng'
-- user_id 2: 'Trß║ºn V─ân Quß║ún L├¢'
-- user_id 3: 'L├¬ Thß╗ï Lß╗à T├ón'
-- user_id 4: 'Nguyß╗àn V─ân Kß╗╣ Thuß║¡t'
-- user_id 5: 'Phß║ím Minh Kh├ích'
-- user_id 6: 'T├ái Khoß║ún Bß╗ï Kh├│a'

-- 2. Bảng service_bays (trước khi sửa):
-- bay_id 1: 'Khoang Rß╗¡a Xe 01 (Bay 1)'
-- bay_id 2: 'Khoang Rß╗¡a Xe 02 (Bay 2)'
-- bay_id 5: 'Cß║ºu N├óng Kß╗╣ Thuß║¡t 01 (Bay 5)'

-- 3. Bảng roles (trước khi sửa):
-- role_id 1: description = 'Quß║ún trß╗ï vi├¬n to├án hß╗ç thß╗æng'
-- role_id 2: description = 'Quß║ún l├¢ cãí sß╗ƒ dß╗ïch vß╗Ñ'
-- role_id 3: description = 'Nh├ón vi├¬n lß╗à t├ón tiß║┐p nhß║¡n & ─æiß╗üu phß╗æi'
-- role_id 4: description = 'Kß╗╣ thuß║¡t vi├¬n ch─âm s├│c xe'
-- role_id 5: description = 'Kh├ích h├áng sß╗¡ dß╗Ñng dß╗ïch vß╗Ñ'

-- 4. Bảng employees (trước khi sửa):
-- employee_id 1: 'Trß║ºn V─ân Quß║ún L├¢', position = 'Quß║ún l├¢ cãí sß╗ƒ'
-- employee_id 2: 'L├¬ Thß╗ï Lß╗à T├ón', position = 'Nh├ón vi├¬n lß╗à t├ón'
-- employee_id 3: 'Nguyß╗àn V─ân Kß╗╣ Thuß║¡t', position = 'Kß╗╣ thuß║¡t vi├¬n Detailing'

-- 5. Bảng customers (trước khi sửa):
-- customer_id 1: 'Phß║ím Minh Kh├ích', address = '123 Nguyß╗àn V─ân Cß╗½, Quß║¡n 5, TP.HCM'

-- 6. Bảng services (trước khi sửa):
-- service_id 1: 'Rß╗¡a xe chi tiß║┐t & H├║t bß╗Ñi nß╗Öi thß║Ñt'
-- service_id 2: '─É├ính b├│ng sãín & Phß╗º Ceramic cao cß║Ñp'
-- service_id 3: 'Vß╗ç sinh khoang m├íy chuy├¬n s├óu'
-- service_id 4: 'Khß╗¡ m├╣i ozon & Diß╗çt khuß║®n nß╗Öi thß║Ñt'
-- service_id 5: 'Thay dß║ºu nhß╗øt & Bß║úo dã░ß╗íng ─æß╗ïnh kß╗│'

-- 7. Bảng staff (trước khi sửa):
-- staff_id 1: position = 'Kß╗╣ thuß║¡t vi├¬n Detailing', experience_level = 'Chuy├¬n vi├¬n bß║¡c 3'
