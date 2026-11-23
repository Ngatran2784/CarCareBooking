const vehicleOptions = Array.from(document.querySelectorAll('.vehicle-option'));
const normalizeVehicleText = value => value.toLocaleLowerCase('vi').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd');
function updateVehicleSummary() {
    const selected = document.querySelector('input[name="vehicleId"]:checked');
    document.getElementById('selectedVehicleName').textContent = selected ? selected.dataset.name : 'Chưa chọn phương tiện';
    document.getElementById('selectedVehiclePlate').textContent = selected ? selected.dataset.plate : '';
    document.getElementById('confirmVehicle').disabled = !selected;
}
vehicleOptions.forEach(option => option.querySelector('input').addEventListener('change', updateVehicleSummary));
document.getElementById('vehicleSearch').addEventListener('input', event => {
    const term = normalizeVehicleText(event.target.value.trim());
    vehicleOptions.forEach(option => { option.hidden = !normalizeVehicleText(option.textContent).includes(term); });
    document.getElementById('vehicleNoResults').hidden = !vehicleOptions.length || vehicleOptions.some(option => !option.hidden);
});
updateVehicleSummary();
