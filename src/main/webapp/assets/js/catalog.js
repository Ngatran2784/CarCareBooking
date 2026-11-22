document.querySelectorAll('.catalog-delete').forEach(form => {
    form.addEventListener('submit', event => {
        if (!window.confirm('Bạn có chắc muốn xóa bản ghi này? Dữ liệu đã sử dụng sẽ được bảo vệ.')) {
            event.preventDefault();
        }
    });
});
document.querySelectorAll('[data-filter-table]').forEach(input => {
    input.addEventListener('input', () => {
        const term = input.value.trim().toLocaleLowerCase('vi');
        document.querySelectorAll('#' + input.dataset.filterTable + ' tbody tr[data-record-id]').forEach(row => {
            row.hidden = !row.textContent.toLocaleLowerCase('vi').includes(term);
        });
    });
});
