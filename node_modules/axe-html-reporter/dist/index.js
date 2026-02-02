"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.createHtmlReport = void 0;
const mustache_1 = __importDefault(require("mustache"));
const loadTemplate_1 = require("./util/loadTemplate");
const prepareReportData_1 = require("./util/prepareReportData");
const prepareAxeRules_1 = require("./util/prepareAxeRules");
const saveHtmlReport_1 = require("./util/saveHtmlReport");
function createHtmlReport({ results, options }) {
    var _a;
    if (!results.violations) {
        throw new Error("'violations' is required for HTML accessibility report. Example: createHtmlReport({ results : { violations: Result[] } })");
    }
    try {
        const template = (0, loadTemplate_1.loadTemplate)();
        const preparedReportData = (0, prepareReportData_1.prepareReportData)({
            violations: results.violations,
            passes: results.passes,
            incomplete: results.incomplete,
            inapplicable: results.inapplicable,
        });
        const htmlContent = mustache_1.default.render(template, {
            url: results.url,
            violationsSummary: preparedReportData.violationsSummary,
            violations: preparedReportData.violationsSummaryTable,
            violationDetails: preparedReportData.violationsDetails,
            checksPassed: preparedReportData.checksPassed,
            checksIncomplete: preparedReportData.checksIncomplete,
            checksInapplicable: preparedReportData.checksInapplicable,
            hasPassed: Boolean(results.passes),
            hasIncomplete: Boolean(results.incomplete),
            hasInapplicable: Boolean(results.inapplicable),
            incompleteTotal: preparedReportData.checksIncomplete
                ? preparedReportData.checksIncomplete.length
                : 0,
            projectKey: options === null || options === void 0 ? void 0 : options.projectKey,
            customSummary: options === null || options === void 0 ? void 0 : options.customSummary,
            hasAxeRawResults: Boolean(results === null || results === void 0 ? void 0 : results.timestamp),
            rules: (0, prepareAxeRules_1.prepareAxeRules)(((_a = results === null || results === void 0 ? void 0 : results.toolOptions) === null || _a === void 0 ? void 0 : _a.rules) || {}),
        });
        if (!options || options.doNotCreateReportFile === undefined || !options.doNotCreateReportFile) {
            (0, saveHtmlReport_1.saveHtmlReport)({
                htmlContent,
                reportFileName: options === null || options === void 0 ? void 0 : options.reportFileName,
                outputDir: options === null || options === void 0 ? void 0 : options.outputDir,
                outputDirPath: options === null || options === void 0 ? void 0 : options.outputDirPath
            });
        }
        return htmlContent;
    }
    catch (e) {
        // @ts-ignore
        console.warn(`HTML report was not created due to the error ${e.message}`);
        // @ts-ignore
        return `Failed to create HTML report due to an error ${e.message}`;
    }
}
exports.createHtmlReport = createHtmlReport;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiaW5kZXguanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi9zcmMvaW5kZXgudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7O0FBQUEsd0RBQWdDO0FBRWhDLHNEQUFtRDtBQUNuRCxnRUFBNkQ7QUFDN0QsNERBQXlEO0FBQ3pELDBEQUF1RDtBQXVCdkQsU0FBZ0IsZ0JBQWdCLENBQUMsRUFBRSxPQUFPLEVBQUUsT0FBTyxFQUFnQjs7SUFDL0QsSUFBSSxDQUFDLE9BQU8sQ0FBQyxVQUFVLEVBQUU7UUFDckIsTUFBTSxJQUFJLEtBQUssQ0FDWCwySEFBMkgsQ0FDOUgsQ0FBQztLQUNMO0lBQ0QsSUFBSTtRQUNBLE1BQU0sUUFBUSxHQUFHLElBQUEsMkJBQVksR0FBRSxDQUFDO1FBQ2hDLE1BQU0sa0JBQWtCLEdBQUcsSUFBQSxxQ0FBaUIsRUFBQztZQUN6QyxVQUFVLEVBQUUsT0FBTyxDQUFDLFVBQVU7WUFDOUIsTUFBTSxFQUFFLE9BQU8sQ0FBQyxNQUFNO1lBQ3RCLFVBQVUsRUFBRSxPQUFPLENBQUMsVUFBVTtZQUM5QixZQUFZLEVBQUUsT0FBTyxDQUFDLFlBQVk7U0FDckMsQ0FBQyxDQUFDO1FBQ0gsTUFBTSxXQUFXLEdBQUcsa0JBQVEsQ0FBQyxNQUFNLENBQUMsUUFBUSxFQUFFO1lBQzFDLEdBQUcsRUFBRSxPQUFPLENBQUMsR0FBRztZQUNoQixpQkFBaUIsRUFBRSxrQkFBa0IsQ0FBQyxpQkFBaUI7WUFDdkQsVUFBVSxFQUFFLGtCQUFrQixDQUFDLHNCQUFzQjtZQUNyRCxnQkFBZ0IsRUFBRSxrQkFBa0IsQ0FBQyxpQkFBaUI7WUFDdEQsWUFBWSxFQUFFLGtCQUFrQixDQUFDLFlBQVk7WUFDN0MsZ0JBQWdCLEVBQUUsa0JBQWtCLENBQUMsZ0JBQWdCO1lBQ3JELGtCQUFrQixFQUFFLGtCQUFrQixDQUFDLGtCQUFrQjtZQUN6RCxTQUFTLEVBQUUsT0FBTyxDQUFDLE9BQU8sQ0FBQyxNQUFNLENBQUM7WUFDbEMsYUFBYSxFQUFFLE9BQU8sQ0FBQyxPQUFPLENBQUMsVUFBVSxDQUFDO1lBQzFDLGVBQWUsRUFBRSxPQUFPLENBQUMsT0FBTyxDQUFDLFlBQVksQ0FBQztZQUM5QyxlQUFlLEVBQUUsa0JBQWtCLENBQUMsZ0JBQWdCO2dCQUNoRCxDQUFDLENBQUMsa0JBQWtCLENBQUMsZ0JBQWdCLENBQUMsTUFBTTtnQkFDNUMsQ0FBQyxDQUFDLENBQUM7WUFDUCxVQUFVLEVBQUUsT0FBTyxhQUFQLE9BQU8sdUJBQVAsT0FBTyxDQUFFLFVBQVU7WUFDL0IsYUFBYSxFQUFFLE9BQU8sYUFBUCxPQUFPLHVCQUFQLE9BQU8sQ0FBRSxhQUFhO1lBQ3JDLGdCQUFnQixFQUFFLE9BQU8sQ0FBQyxPQUFPLGFBQVAsT0FBTyx1QkFBUCxPQUFPLENBQUUsU0FBUyxDQUFDO1lBQzdDLEtBQUssRUFBRSxJQUFBLGlDQUFlLEVBQUMsQ0FBQSxNQUFBLE9BQU8sYUFBUCxPQUFPLHVCQUFQLE9BQU8sQ0FBRSxXQUFXLDBDQUFFLEtBQUssS0FBSSxFQUFFLENBQUM7U0FDNUQsQ0FBQyxDQUFDO1FBQ0gsSUFBSSxDQUFDLE9BQU8sSUFBSSxPQUFPLENBQUMscUJBQXFCLEtBQUssU0FBUyxJQUFJLENBQUMsT0FBTyxDQUFDLHFCQUFxQixFQUFFO1lBQzNGLElBQUEsK0JBQWMsRUFBQztnQkFDWCxXQUFXO2dCQUNYLGNBQWMsRUFBRSxPQUFPLGFBQVAsT0FBTyx1QkFBUCxPQUFPLENBQUUsY0FBYztnQkFDdkMsU0FBUyxFQUFFLE9BQU8sYUFBUCxPQUFPLHVCQUFQLE9BQU8sQ0FBRSxTQUFTO2dCQUM3QixhQUFhLEVBQUUsT0FBTyxhQUFQLE9BQU8sdUJBQVAsT0FBTyxDQUFFLGFBQWE7YUFDeEMsQ0FBQyxDQUFDO1NBQ047UUFFRCxPQUFPLFdBQVcsQ0FBQztLQUN0QjtJQUFDLE9BQU8sQ0FBQyxFQUFFO1FBQ1IsYUFBYTtRQUNiLE9BQU8sQ0FBQyxJQUFJLENBQUMsZ0RBQWdELENBQUMsQ0FBQyxPQUFPLEVBQUUsQ0FBQyxDQUFDO1FBRTFFLGFBQWE7UUFDYixPQUFPLGdEQUFnRCxDQUFDLENBQUMsT0FBTyxFQUFFLENBQUM7S0FDdEU7QUFDTCxDQUFDO0FBbERELDRDQWtEQyJ9