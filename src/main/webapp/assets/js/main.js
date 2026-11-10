// CarCareBooking - Global Script
document.addEventListener('DOMContentLoaded', function() {
    console.log('CarCareBooking loaded successfully.');
    
    // Auto highlight active navigation link based on current path
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.sidebar .nav-link');
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.endsWith(href)) {
            link.classList.add('active');
        }
    });
});
