import { createHtmlReport } from 'axe-html-reporter';
import { readFileSync } from 'fs';

(() => {
    const rawAxeResults = JSON.parse(readFileSync('AxeResults.json', 'utf8'))
    createHtmlReport({
        results: rawAxeResults,
        options: {}
    });
})();