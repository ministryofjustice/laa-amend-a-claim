import * as GOVUKFrontend from './govuk-frontend.min.js';
import * as MOJFrontend from './moj-frontend.min.js';

document.body.className += ` js-enabled${'noModule' in HTMLScriptElement.prototype ? ' govuk-frontend-supported' : ''}`;

GOVUKFrontend.initAll();
MOJFrontend.initAll();

document.querySelectorAll<HTMLAnchorElement>('[data-module="back-link"]').forEach(function (link: HTMLAnchorElement): void {
    link.addEventListener('click', function (e: MouseEvent): void {
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
    const id = select.id;
    const options = Array.from(select.options)
        .filter((option: HTMLOptionElement) => option.value)
        .map((option: HTMLOptionElement) => option.textContent || option.innerText);

    accessibleAutocomplete.enhanceSelectElement({
        selectElement: select,
        defaultValue: select.value,
        allowEmpty: true,
        displayMenu: 'overlay',
        // Match the query anywhere in the text, but list matches at the start first, alphabetically.
        source: function (query: string, populateResults: (results: string[]) => void): void {
            const lowerQuery = query.toLowerCase();
            const results = options.filter((option: string) => option.toLowerCase().includes(lowerQuery));
            results.sort((a: string, b: string) => {
                const aStartsWith = a.toLowerCase().startsWith(lowerQuery);
                const bStartsWith = b.toLowerCase().startsWith(lowerQuery);
                if (aStartsWith !== bStartsWith) {
                    return aStartsWith ? -1 : 1;
                }
                return a.localeCompare(b);
            });
            populateResults(results);
        }
    });

    const input = document.getElementById(id) as HTMLInputElement;
    // The library only updates the select when an option is confirmed, so clearing the text
    // would otherwise leave the previous value submitted.
    input.addEventListener('input', function (): void {
        if (!input.value.trim()) {
            select.value = '';
            select.dispatchEvent(new Event('change'));
        }
    });
    // Mark the prefilled option as valid so focusing selects it without reopening the menu.
    input.dispatchEvent(new FocusEvent('focus'));
    input.dispatchEvent(new FocusEvent('blur'));
});