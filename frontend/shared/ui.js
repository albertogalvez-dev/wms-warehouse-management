/* ============================================
   WMS Shared UI Helpers
   ============================================ */

// Toast container (created on first use)
let toastContainer = null;

function getToastContainer() {
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }
    return toastContainer;
}

/**
 * Show a toast notification
 * @param {string} type - 'ok' | 'err' | 'warn' | 'info'
 * @param {string} message - Toast message
 * @param {number} duration - Duration in ms (default 3000)
 */
export function showToast(type, message, duration = 3000) {
    const container = getToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    const icons = {
        ok: '✓',
        err: '✕',
        warn: '⚠',
        info: 'ℹ'
    };

    toast.innerHTML = `
    <span style="font-size:1.1em">${icons[type] || ''}</span>
    <span>${escapeHtml(message)}</span>
  `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(20px)';
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

// Shortcuts
export const toastOk = (msg) => showToast('ok', msg);
export const toastErr = (msg) => showToast('err', msg);
export const toastWarn = (msg) => showToast('warn', msg);

// Modal state
let modalBackdrop = null;

/**
 * Open a modal dialog
 * @param {Object} options - { title, bodyHtml, onClose }
 */
export function openModal({ title = '', bodyHtml = '', onClose = null }) {
    closeModal(); // Close any existing

    modalBackdrop = document.createElement('div');
    modalBackdrop.className = 'modal-backdrop';

    modalBackdrop.innerHTML = `
    <div class="modal" role="dialog" aria-modal="true">
      <div class="modal-header">
        <h3 class="modal-title">${escapeHtml(title)}</h3>
        <button class="modal-close" aria-label="Close">&times;</button>
      </div>
      <div class="modal-body">${bodyHtml}</div>
    </div>
  `;

    document.body.appendChild(modalBackdrop);
    document.body.style.overflow = 'hidden';

    // Close handlers
    const closeBtn = modalBackdrop.querySelector('.modal-close');
    closeBtn.addEventListener('click', () => {
        closeModal();
        if (onClose) onClose();
    });

    modalBackdrop.addEventListener('click', (e) => {
        if (e.target === modalBackdrop) {
            closeModal();
            if (onClose) onClose();
        }
    });

    // ESC key
    const escHandler = (e) => {
        if (e.key === 'Escape') {
            closeModal();
            if (onClose) onClose();
            document.removeEventListener('keydown', escHandler);
        }
    };
    document.addEventListener('keydown', escHandler);
}

/**
 * Close the current modal
 */
export function closeModal() {
    if (modalBackdrop) {
        modalBackdrop.remove();
        modalBackdrop = null;
        document.body.style.overflow = '';
    }
}

/**
 * Format ISO date string to readable format
 * @param {string} iso - ISO date string
 * @returns {string} Formatted date
 */
export function formatDate(iso) {
    if (!iso) return '';
    try {
        const d = new Date(iso);
        return d.toLocaleDateString('es-ES', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch {
        return iso;
    }
}

/**
 * Format date as short (date only)
 */
export function formatDateShort(iso) {
    if (!iso) return '';
    try {
        return new Date(iso).toLocaleDateString('es-ES');
    } catch {
        return iso;
    }
}

/**
 * Get badge HTML for a status
 * @param {string} status - Status string
 * @returns {string} Badge HTML
 */
export function badgeForStatus(status) {
    const statusMap = {
        // Order statuses
        'DRAFT': 'badge-neutral',
        'RELEASED': 'badge-info',
        'PICKING': 'badge-warn',
        'PICKED': 'badge-ok',
        'PACKING': 'badge-warn',
        'PACKED': 'badge-ok',
        'SHIPPED': 'badge-blue',

        // Wave statuses
        'PLANNED': 'badge-neutral',
        'IN_PROGRESS': 'badge-warn',
        'DONE': 'badge-ok',
        'CANCELLED': 'badge-err',

        // Tote statuses
        'OPEN': 'badge-info',
        'AT_PACKING': 'badge-warn',
        'CLOSED': 'badge-ok',

        // Shipment statuses
        'CREATED': 'badge-neutral',
        'LABELLED': 'badge-info',
        'PRINTED': 'badge-ok',

        // Session statuses
        'SCAN_ITEMS': 'badge-info',
        'SET_PACKAGES': 'badge-warn',
        'READY_TO_COMPLETE': 'badge-ok'
    };

    const cls = statusMap[status] || 'badge-neutral';
    return `<span class="badge ${cls}">${escapeHtml(status)}</span>`;
}

/**
 * Escape HTML special characters
 */
export function escapeHtml(str) {
    if (str == null) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

/**
 * Download text content as a file
 */
export function downloadText(filename, content) {
    const blob = new Blob([content], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
}

/**
 * DOM query shortcuts
 */
export const qs = (sel, root = document) => root.querySelector(sel);
export const qsa = (sel, root = document) => [...root.querySelectorAll(sel)];

/**
 * Debounce function
 */
export function debounce(fn, delay = 300) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => fn(...args), delay);
    };
}
