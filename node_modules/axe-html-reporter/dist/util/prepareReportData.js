"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.prepareReportData = void 0;
const getWcagReference_1 = require("./getWcagReference");
function simplifyAxeResultForSummary(results) {
    return results.map(({ nodes, description, help, id, tags, impact }, resultIndex) => ({
        index: resultIndex + 1,
        description,
        id,
        help,
        wcag: (0, getWcagReference_1.getWcagReference)(tags),
        tags,
        impact: impact || 'n/a',
        nodes: nodes.length,
    }));
}
function prepareFixSummary(failureSummary, defaultHighlight) {
    const failureSummariesSplit = failureSummary.split('\n\n');
    return failureSummariesSplit.map((summary) => {
        const fixSummarySplit = summary.split('\n');
        if (fixSummarySplit.length == 0) {
            return defaultHighlight;
        }
        else {
            return {
                highlight: fixSummarySplit.shift() || '',
                list: fixSummarySplit,
            };
        }
    });
}
/**
 * Prepare report splitting it into sections:
 * - total accessibility violations (counting nodes)
 * - summary of violations that could be printed as table
 * - detailed list of violations that could be printed as formatted text
 */
function prepareReportData({ violations, passes, incomplete, inapplicable, }) {
    const passedChecks = passes ? simplifyAxeResultForSummary(passes) : undefined;
    const incompleteChecks = incomplete ? simplifyAxeResultForSummary(incomplete) : undefined;
    const inapplicableChecks = inapplicable ? simplifyAxeResultForSummary(inapplicable) : undefined;
    const violationsTotal = violations.reduce((acc, { nodes }) => {
        acc += nodes.length;
        return acc;
    }, 0);
    if (violations.length === 0) {
        return {
            violationsSummary: 'axe-core found <span class="badge badge-success">0</span> violations',
            checksPassed: passedChecks,
            checksIncomplete: incompleteChecks,
            checksInapplicable: inapplicableChecks,
        };
    }
    const violationsSummary = `axe-core found <span class="badge badge-warning">${violationsTotal}</span> violation${violationsTotal === 1 ? '' : 's'}`;
    // Prepare data to show summary
    const violationsSummaryTable = simplifyAxeResultForSummary(violations);
    // Prepare data to show detailed list of violations
    const violationsDetails = violations.map(({ nodes, impact, description, help, id, tags, helpUrl }, issueIndex) => {
        return {
            index: issueIndex + 1,
            wcag: (0, getWcagReference_1.getWcagReference)(tags),
            tags,
            id,
            impact: impact || 'n/a',
            description,
            help,
            helpUrl,
            nodes: nodes.map(({ target, html, failureSummary, any }, nodeIndex) => {
                const targetNodes = target.join('\n');
                const defaultHighlight = {
                    highlight: 'Recommendation with the fix was not provided by axe result',
                };
                const fixSummaries = failureSummary
                    ? prepareFixSummary(failureSummary, defaultHighlight)
                    : [defaultHighlight];
                const relatedNodesAny = [];
                any.forEach((checkResult) => {
                    if (checkResult.relatedNodes && checkResult.relatedNodes.length > 0) {
                        checkResult.relatedNodes.forEach((node) => {
                            if (node.target.length > 0) {
                                relatedNodesAny.push(node.target.join('\n'));
                            }
                        });
                    }
                });
                return {
                    targetNodes,
                    html,
                    fixSummaries,
                    relatedNodesAny,
                    index: nodeIndex + 1,
                };
            }),
        };
    });
    return {
        violationsSummary,
        violationsSummaryTable,
        violationsDetails,
        checksPassed: passedChecks,
        checksIncomplete: incompleteChecks,
        checksInapplicable: inapplicableChecks,
    };
}
exports.prepareReportData = prepareReportData;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoicHJlcGFyZVJlcG9ydERhdGEuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvdXRpbC9wcmVwYXJlUmVwb3J0RGF0YS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFDQSx5REFBc0Q7QUFJdEQsU0FBUywyQkFBMkIsQ0FBQyxPQUFpQjtJQUNsRCxPQUFPLE9BQU8sQ0FBQyxHQUFHLENBQUMsQ0FBQyxFQUFFLEtBQUssRUFBRSxXQUFXLEVBQUUsSUFBSSxFQUFFLEVBQUUsRUFBRSxJQUFJLEVBQUUsTUFBTSxFQUFFLEVBQUUsV0FBVyxFQUFFLEVBQUUsQ0FBQyxDQUFDO1FBQ2pGLEtBQUssRUFBRSxXQUFXLEdBQUcsQ0FBQztRQUN0QixXQUFXO1FBQ1gsRUFBRTtRQUNGLElBQUk7UUFDSixJQUFJLEVBQUUsSUFBQSxtQ0FBZ0IsRUFBQyxJQUFJLENBQUM7UUFDNUIsSUFBSTtRQUNKLE1BQU0sRUFBRSxNQUFNLElBQUksS0FBSztRQUN2QixLQUFLLEVBQUUsS0FBSyxDQUFDLE1BQU07S0FDdEIsQ0FBQyxDQUFDLENBQUM7QUFDUixDQUFDO0FBRUQsU0FBUyxpQkFBaUIsQ0FBQyxjQUFzQixFQUFFLGdCQUE0QjtJQUMzRSxNQUFNLHFCQUFxQixHQUFHLGNBQWMsQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLENBQUM7SUFDM0QsT0FBTyxxQkFBcUIsQ0FBQyxHQUFHLENBQUMsQ0FBQyxPQUFPLEVBQUUsRUFBRTtRQUN6QyxNQUFNLGVBQWUsR0FBRyxPQUFPLENBQUMsS0FBSyxDQUFDLElBQUksQ0FBQyxDQUFDO1FBQzVDLElBQUksZUFBZSxDQUFDLE1BQU0sSUFBSSxDQUFDLEVBQUU7WUFDN0IsT0FBTyxnQkFBZ0IsQ0FBQztTQUMzQjthQUFNO1lBQ0gsT0FBTztnQkFDSCxTQUFTLEVBQUUsZUFBZSxDQUFDLEtBQUssRUFBRSxJQUFJLEVBQUU7Z0JBQ3hDLElBQUksRUFBRSxlQUFlO2FBQ3hCLENBQUM7U0FDTDtJQUNMLENBQUMs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