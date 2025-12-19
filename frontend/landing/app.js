/* ============================================
   WMS Landing Page JavaScript
   ============================================ */

// Smooth scroll for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        const targetId = this.getAttribute('href');
        if (targetId === '#') return;

        const target = document.querySelector(targetId);
        if (target) {
            e.preventDefault();
            const topbarHeight = 64;
            const targetPosition = target.getBoundingClientRect().top + window.pageYOffset - topbarHeight;

            window.scrollTo({
                top: targetPosition,
                behavior: 'smooth'
            });
        }
    });
});

// Add scrolled class to topbar
const topbar = document.querySelector('.topbar');
window.addEventListener('scroll', () => {
    if (window.scrollY > 10) {
        topbar.classList.add('scrolled');
    } else {
        topbar.classList.remove('scrolled');
    }
});

// Optional: Animate elements on scroll
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('visible');
        }
    });
}, observerOptions);

// Observe feature cards and timeline items
document.querySelectorAll('.feature-card, .timeline-item, .integration-card').forEach(el => {
    el.classList.add('animate-on-scroll');
    observer.observe(el);
});

// Add CSS for animations dynamically
const style = document.createElement('style');
style.textContent = `
  .animate-on-scroll {
    opacity: 0;
    transform: translateY(20px);
    transition: opacity 0.5s ease, transform 0.5s ease;
  }
  .animate-on-scroll.visible {
    opacity: 1;
    transform: translateY(0);
  }
  .topbar.scrolled {
    box-shadow: var(--shadow-md);
  }
`;
document.head.appendChild(style);

console.log('WMS Landing Page loaded');
