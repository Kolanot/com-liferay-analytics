/**
 *
 * @param {*} analytics
 */
function onload(analytics) {
	const perfData = window.performance.timing;
	const pageLoadTime = perfData.loadEventStart - perfData.navigationStart;
	const props = {
		pageLoadTime,
	};
	analytics.send('load', 'timing', props);
}

/**
 *
 * @param {*} analytics
 */
function unload(analytics) {
	const perfData = window.performance.timing;
	const viewDuration = new Date().getTime() - perfData.navigationStart;
	const props = {
		viewDuration,
	};
	analytics.send('unload', 'timing', props);
}

/**
 * Plugin function that registers listeners against browser time events
 * @param {object} analytics
 */
function timing(analytics) {
	window.addEventListener('load', onload.bind(null, analytics));
	window.addEventListener('unload', unload.bind(null, analytics));
}

export {timing};
export default timing;