const fs = require('fs');
const { createHtmlReport } = require('axe-html-reporter');

const inputFile = process.argv[2];
const reportFileName = process.argv[3];

const results = JSON.parse(fs.readFileSync(inputFile, 'utf8'));

createHtmlReport({
    results,
    options: {
        outputDir: "build/axe-reports/html",
        reportFileName: reportFileName
    },
});