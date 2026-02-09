const fs = require('fs');
const path = require('path');
const { createHtmlReport } = require('axe-html-reporter');

const inputDir = 'build/axe-reports/json';

const files = fs.readdirSync(inputDir, { withFileTypes: true })
    .filter(entry => entry.isFile())
    .map(entry => entry.name)
    .filter(name => name.endsWith('.json'));

for (const file of files) {
    const inputPath = path.join(inputDir, file);
    const results = JSON.parse(fs.readFileSync(inputPath, 'utf8'));

    const reportFileName = file.replace(/\.json$/, '.html');

    createHtmlReport({
        results,
        options: {
            outputDir: 'build/axe-reports/html',
            reportFileName: reportFileName
        },
    });
}