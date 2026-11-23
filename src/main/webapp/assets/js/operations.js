document.querySelectorAll('.delete-form').forEach(form => form.addEventListener('submit', event => {
    if (!confirm('Bạn có chắc muốn xóa dữ liệu này?')) event.preventDefault();
}));
function fillOperationsForm(form, values) {
    const panel = form.closest('details');
    if (panel) panel.open = true;
    Object.entries(values).forEach(([key, value]) => {
        if (form.elements.namedItem(key)) form.elements.namedItem(key).value = value;
    });
    form.querySelector('select, input:not([type=hidden])')?.focus({preventScroll:true});
    form.scrollIntoView({behavior: 'smooth', block: 'center'});
}
document.querySelectorAll('.edit-shift').forEach(button => button.addEventListener('click', () => {
    const d = button.dataset;
    fillOperationsForm(document.getElementById('shiftForm'), {id:d.id, staffId:d.staff, date:d.date, start:d.start, end:d.end, status:d.status});
}));
document.querySelectorAll('.edit-skill').forEach(button => button.addEventListener('click', () => {
    const d = button.dataset;
    fillOperationsForm(document.getElementById('skillForm'), {id:d.id, staffId:d.staff, serviceId:d.service, level:d.level});
}));
document.querySelectorAll('#shiftForm, #skillForm').forEach(form => form.addEventListener('reset', () => {
    form.elements.namedItem('id').value = '0';
}));
const submitted = document.getElementById('submittedForm');
if (submitted) {
    const values = Object.fromEntries(Array.from(submitted.children, item => [item.dataset.key, item.dataset.value]));
    const form = document.getElementById(values.action === 'shift' ? 'shiftForm' : values.action === 'skill' ? 'skillForm' : '');
    if (form) fillOperationsForm(form, values);
}
