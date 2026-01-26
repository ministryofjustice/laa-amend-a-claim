import * as GOVUKFrontend from '/webjars/govuk-frontend/dist/govuk/govuk-frontend.min.js';
import * as MOJFrontend from '/webjars/ministryofjustice__frontend/moj/moj-frontend.min.js';

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