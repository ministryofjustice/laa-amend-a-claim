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
    const defaultValue = select.options[select.options.selectedIndex].innerHTML;

    accessibleAutocomplete.enhanceSelectElement({
        element: select,
        id: select.id,
        defaultValue: defaultValue,
        selectElement: select,
        allowEmpty: true,
        displayMenu: 'overlay',
        showAllValues: true,
        dropdownArrow: () => `<svg class="autocomplete__dropdown-arrow-down" style="top: 8px;" viewBox="0 0 512 512"><path d="M256,298.3L256,298.3L256,298.3l174.2-167.2c4.3-4.2,11.4-4.1,15.8,0.2l30.6,29.9c4.4,4.3,4.5,11.3,0.2,15.5L264.1,380.9  c-2.2,2.2-5.2,3.2-8.1,3c-3,0.1-5.9-0.9-8.1-3L35.2,176.7c-4.3-4.2-4.2-11.2,0.2-15.5L66,131.3c4.4-4.3,11.5-4.4,15.8-0.2L256,298.3  z"></path></svg>`,
        // Match the query anywhere in the text, but list matches at the start first, alphabetically.
        // Treat a query equal to the pre-filled default value as "not actively searching" so that
        // showAllValues shows the full list rather than just the already-selected option.
        source: function (query: string, populateResults: (results: string[]) => void): void {
            const isUnfilteredQuery = query === '' || query === defaultValue;
            const lowerQuery = isUnfilteredQuery ? '' : query.toLowerCase();
            const options = [].filter.call(select.options, (option: HTMLOptionElement) => option.value)
                .map((option: HTMLOptionElement) => option.textContent || option.innerText);
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
});