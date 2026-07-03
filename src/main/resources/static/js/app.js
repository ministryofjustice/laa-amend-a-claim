import * as GOVUKFrontend from '/webjars/govuk-frontend/dist/govuk/govuk-frontend.min.js';
import * as MOJFrontend from '/webjars/ministryofjustice__frontend/moj/moj-frontend.min.js';
import '/webjars/accessible-autocomplete/dist/accessible-autocomplete.min.js'

document.body.className += ' js-enabled' + ('noModule' in HTMLScriptElement.prototype ? ' govuk-frontend-supported' : '');

GOVUKFrontend.initAll();
MOJFrontend.initAll();

document.querySelectorAll('[data-module="back-link"]').forEach(function(link) {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        window.history.back();
    });
});

document.querySelectorAll('[data-module="logout-link"]').forEach(function(link) {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        document.logoutForm.submit();
    });
});


document.querySelectorAll('[id=bulk-upload-form]').forEach(function (form) {
    form.addEventListener('submit', function () {
        form.querySelector('#bulk-upload-button').disabled = true;
        form.querySelector('#loading-wrapper').style.display = "block";
        form.querySelector('#drop-zone').style.display = "none";
    });
});

var selectDropdowns = document.querySelectorAll('[data-module="make-autocomplete"]');

// For each dropdown
selectDropdowns.forEach(function(select) {
    accessibleAutocomplete.enhanceSelectElement({
        element: select,
        id: select.id,
        defaultValue: select.options[select.options.selectedIndex].innerHTML,
        selectElement: select,
        allowEmpty: true
    });
});