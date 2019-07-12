'use strict';

define('fcf-smart', [
	'fujion-core', 
	'fujion-widget',
	'fcf-smart-css'], function(fujion, Widget, Hashes) {

	var _windows = [];

	/******************************************************************************************************************
	 * A CKEditor widget
	 ******************************************************************************************************************/ 
	Widget.SmartContainer = Widget.UIWidget.extend({

		_contentWindow: null,

		/*------------------------------ Events ------------------------------*/

		sendRequest: function(data) {
			if (data && data.messageId && data.messageType) {
				this.trigger('smart_request', {data: data});
			}
		},

		handleResponse: function(event) {
			this._postMessage(event.payload || event.data);
		},

		/*------------------------------ Lifecycle ------------------------------*/
		
		destroy: function() {
			this._unregisterWindow();
			this._super();
		},
		
		init: function() {
			this._super();
			this.forwardToServer('smart_request');
		},
		
		/*------------------------------ Other ------------------------------*/

		_postMessage: function(message) {
			if (message && this._contentWindow) {
				this._contentWindow.postMessage(message, "*");
			}
		},

		_registerWindow: function(window) {
			this._unregisterWindow();
			registerWindow(window, this);
			this._contentWindow = window;
		},

		_unregisterWindow: function() {
			unregisterWindow(this._contentWindow);
			this._contentWindow = null;
		},

		/*------------------------------ Rendering ------------------------------*/

		afterRender: function() {
			this._super();
			this.widget$.on('smart_response', this.handleResponse.bind(this));
			this._registerWindow(this.widget$.get(0).contentWindow);
		},

		render$: function() {
			return $('<iframe>');
		},
		
		/*------------------------------ State ------------------------------*/

		s_src: function(v) {
			this.attr('src', v);
		}
	});

	window.addEventListener("message", processRequest, false);

	function processRequest(event) {
		var index = findWindow(event.source);

		if (index > -1) {
			var widget = _windows[index].widget;
			widget.sendRequest(event.data);
		}
	}

	function findWindow(window) {
		return window ? _.findIndex(_windows, function(entry) { return entry.window === window}) : -1;
	}

	function registerWindow(window, widget) {
		_windows.push({window: window, widget: widget});
	}

	function unregisterWindow(window) {
		var index = findWindow(window);

		if (index > -1) {
			_windows.splice(index, 1);
		}
	}

	return fujion.widget;
});


