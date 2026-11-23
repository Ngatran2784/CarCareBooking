document.querySelectorAll('.catalog-delete').forEach(form => {
    form.addEventListener('submit', event => {
        if (!window.confirm('Bạn có chắc muốn xóa bản ghi này? Dữ liệu đã sử dụng sẽ được bảo vệ.')) {
            event.preventDefault();
        }
    });
});

const normalizeCatalogText = value => value.toLocaleLowerCase('vi').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd');
document.querySelectorAll('[data-filter-table]').forEach(input => {
    const table = document.getElementById(input.dataset.filterTable);
    const status = document.querySelector('[data-status-filter="' + table.id + '"]');
    const rows = Array.from(table.querySelectorAll('tbody tr[data-record-id]'));
    const message = document.createElement('p');
    message.className = 'empty-result';
    message.textContent = table.dataset.emptyMessage || 'Không tìm thấy dữ liệu phù hợp.';
    message.hidden = true;
    message.setAttribute('role', 'status');
    table.parentElement.after(message);
    const update = () => {
        const term = normalizeCatalogText(input.value.trim());
        rows.forEach(row => { row.hidden = !normalizeCatalogText(row.textContent).includes(term) || Boolean(status && status.value && row.dataset.status !== status.value); });
        const count = rows.filter(row => !row.hidden).length;
        message.hidden = !rows.length || count > 0;
        const counter = document.querySelector('[data-result-count="' + table.id + '"]');
        if (counter) counter.textContent = count + ' / ' + rows.length + ' dịch vụ';
    };
    input.addEventListener('input', update);
    if (status) status.addEventListener('change', update);
    update();
});
document.querySelectorAll('[data-open-editor]').forEach(button => button.addEventListener('click', () => {
    const panel = document.getElementById(button.dataset.openEditor);
    panel.open = true;
    panel.scrollIntoView({behavior: 'smooth', block: 'start'});
    panel.querySelector('input:not([type="hidden"]), select')?.focus({preventScroll:true});
}));
