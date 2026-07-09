import * as GOVUKFrontend from './govuk-frontend.min.js';
import * as MOJFrontend from './moj-frontend.min.js';

document.body.className += ` js-enabled${'noModule' in HTMLScriptElement.prototype ? ' govuk-frontend-supported' : ''}`;

GOVUKFrontend.initAll();
MOJFrontend.initAll();

document.querySelectorAll<HTMLAnchorElement>('[data-module="back-link"]').forEach(function (link: HTMLAnchorElement): void {
    link.addEventListener('click', function (e: MouseEvent): void {
        console.log('Back link clicked');
        e.preventDefault();
        window.history.back();
    });
});

document.querySelectorAll<HTMLAnchorElement>('[data-module="logout-link"]').forEach(function (link: HTMLAnchorElement): void {
    link.addEventListener('click', function (e: MouseEvent): void {
        e.preventDefault();
        (document as any).logoutForm.submit();
    });
});


document.querySelectorAll<HTMLFormElement>('[id=bulk-upload-form]').forEach(function (form: HTMLFormElement): void {
    form.addEventListener('submit', function (): void {
        (form.querySelector('#bulk-upload-button') as HTMLButtonElement).disabled = true;
        (form.querySelector('#loading-wrapper') as HTMLElement).style.display = "block";
        (form.querySelector('#drop-zone') as HTMLElement).style.display = "none";
    });
});


const selectDropdowns = document.querySelectorAll<HTMLSelectElement>('[data-module="make-autocomplete"]');

// For each dropdown
selectDropdowns.forEach(function (select: HTMLSelectElement) {
    accessibleAutocomplete.enhanceSelectElement({
        element: select,
        id: select.id,
        defaultValue: select.options[select.options.selectedIndex].innerHTML,
        selectElement: select,
        allowEmpty: true
    });
});