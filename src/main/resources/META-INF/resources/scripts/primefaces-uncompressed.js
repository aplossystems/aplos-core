PrimeFaces = {

    escapeClientId : function(id) {
        return "#" + id.replace(/:/g,"\\:");
    },
	
    onContentReady : function(id, fn) {
        YAHOO.util.Event.onContentReady(id, fn, window, true);
    },
	
    cleanWatermarks : function(){
        $.watermark.hideAll();
    },
	
    showWatermarks : function(){
        $.watermark.showAll();
    },
	
    addSubmitParam : function(parent, params) {
        var form = $(this.escapeClientId(parent));
        form.children('input.ui-submit-param').remove();

        for(var key in params) {
            form.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + params[key] + "\" class=\"ui-submit-param\"/>");
        }

        return this;
    },

    submit : function(formId) {
        $(this.escapeClientId(formId)).submit();
    },

    attachBehaviors : function(element, behaviors) {
        $.each(behaviors, function(event, fn) {
            element.bind(event, function(e) {
                fn.call(element, e);
            });
        });
    },

    getCookie : function(name) {
        return $.cookie(name);
    },

    setCookie : function(name, value) {
        $.cookie(name, value);
    },

    skinInput : function(input) {
        input.hover(
            function() {
                $(this).addClass('ui-state-hover');
            },
            function() {
                $(this).removeClass('ui-state-hover');
            }
        ).focus(function() {
                $(this).addClass('ui-state-focus');
        }).blur(function() {
                $(this).removeClass('ui-state-focus');
        });
    },
    
    skinButton : function(button) {
        button.mouseover(function(){
            var el = $(this);
            if(!button.hasClass('ui-state-disabled')) {
                el.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            $(this).removeClass('ui-state-active ui-state-hover');
        }).mousedown(function() {
            var el = $(this);
            if(!button.hasClass('ui-state-disabled')) {
                el.addClass('ui-state-active');
            }
        }).mouseup(function() {
            $(this).removeClass('ui-state-active');
        });
        
        return this;
    },

    // ajax shortcut
    ab : function(cfg, ext) {
        PrimeFaces.ajax.AjaxRequest(cfg, ext);
    },

    // mobile
    navigate : function(to, cfg) {
        var options = cfg ? cfg : {};
        options.changeHash = false;
        
        $.mobile.changePage(to, options);
    },
    
    info: function(msg) {
        if(window.log) {
            log.info(msg);
        }
    },
    
    debug: function(msg) {
        if(window.log) {
            log.debug(msg);
        }
    },
    
    warn: function(msg) {
        if(window.log) {
            log.warn(msg);
        }
    },
    
    error: function(msg) {
        if(window.log) {
            log.error(msg);
        }
    },
    
    changeTheme: function(newTheme) {
        if(newTheme && newTheme != '') {
            var themeLink = $('link[href*="javax.faces.resource/theme.css"]'),
            themeURL = themeLink.attr('href'),
            plainURL = themeURL.split('&')[0],
            oldTheme = plainURL.split('ln=')[1],
            newThemeURL = themeURL.replace(oldTheme, 'primefaces-' + newTheme);

            themeLink.attr('href', newThemeURL);
        }
    },
    
    clearSelection: function() {
        var sel = window.getSelection ? window.getSelection() : document.selection;
        if(sel) {
            if(sel.removeAllRanges)
                sel.removeAllRanges();
            else if(sel.empty)
                sel.empty();
        }
    },
    
    extend: function(subClass, superClass) {
        subClass.prototype = new superClass;
        subClass.prototype.constructor = subClass;
    },
    
    cw : function(widgetConstructor, widgetVar, cfg, resource) {
        PrimeFaces.createWidget(widgetConstructor, widgetVar, cfg, resource);
    },
    
    createWidget : function(widgetConstructor, widgetVar, cfg, resource) {            
        if(PrimeFaces.widget[widgetConstructor]) {
            window[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
        }
        else {
            var scriptURI = $('script[src*="/javax.faces.resource/primefaces.js"]').attr('src').replace('primefaces.js', resource + '/' + resource + '.js'),
            cssURI = $('link[href*="/javax.faces.resource/primefaces.css"]').attr('href').replace('primefaces.css', resource + '/' + resource + '.css'),
            cssResource = '<link type="text/css" rel="stylesheet" href="' + cssURI + '" />';

            // load css
            $('head').append(cssResource);

            // load script and initialize widget
            PrimeFaces.getScript(location.protocol + '//' + location.host + scriptURI, function() {
                setTimeout(function() {
                    window[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
                }, 100);
            });
        }
    },

    isNumber: function(value) {
        return typeof value === 'number' && isFinite(value);
    },
    
    getScript: function(url, callback) {
        $.ajax({
			type: "GET",
			url: url,
			success: callback,
			dataType: "script",
			cache: true
        });
    },
    
    focus : function(id, context) {
        var selector = ':not(:submit):not(:button):input:visible:enabled:first';

        setTimeout(function() {
            if(id) {
                var jq = $(PrimeFaces.escapeClientId(id));

                if(jq.is(selector)) {
                    jq.focus();
                } 
                else {
                    jq.find(selector).focus();
                }
            }
            else if(context) {
                var jq = $(PrimeFaces.escapeClientId(context));

                jq.find(selector).focus();
            }
            else {
                $(selector).focus();
            }
        }, 250);
    },

    locales : {},
    
    zindex : 1000,
	
    PARTIAL_REQUEST_PARAM : "javax.faces.partial.ajax",

    PARTIAL_UPDATE_PARAM : "javax.faces.partial.render",

    PARTIAL_PROCESS_PARAM : "javax.faces.partial.execute",

    PARTIAL_SOURCE_PARAM : "javax.faces.source",

    BEHAVIOR_EVENT_PARAM : "javax.faces.behavior.event",

    PARTIAL_EVENT_PARAM : "javax.faces.partial.event",

    VIEW_STATE : "javax.faces.ViewState"
};

PrimeFaces.ajax = {};
PrimeFaces.widget = {};
PrimeFaces.websockets = {};

/**
 * BaseWidget for PrimeFaces Widgets to implement common tasks
 */
PrimeFaces.widget.BaseWidget = function() {}

PrimeFaces.widget.BaseWidget.prototype.postConstruct = function() {
    this.getScriptTag().remove();
};

PrimeFaces.widget.BaseWidget.prototype.getScriptTag = function() {
    return $(this.jqId + '_s');
};

PrimeFaces.widget.BaseWidget.prototype.getJQ = function() {
    return this.jq;
};

PrimeFaces.ajax.AjaxUtils = {
	
    encodeViewState : function() {
        var viewstateValue = document.getElementById(PrimeFaces.VIEW_STATE).value;
        var re = new RegExp("\\+", "g");
        var encodedViewState = viewstateValue.replace(re, "\%2B");
		
        return encodedViewState;
    },
	
    updateState: function(value) {
        var viewstateValue = $.trim(value),
        forms = this.portletForms ? this.portletForms : $('form');
        
        forms.each(function() {
            var form = $(this),
            formViewStateElement = form.children("input[name='javax.faces.ViewState']").get(0);

            if(formViewStateElement) {
                $(formViewStateElement).val(viewstateValue);
            }
            else
            {
                form.append('<input type="hidden" name="javax.faces.ViewState" id="javax.faces.ViewState" value="' + viewstateValue + '" autocomplete="off" />');
            }
        });
    },
	
    serialize: function(params) {
        var serializedParams = '';
		
        for(var param in params) {
            serializedParams = serializedParams + "&" + param + "=" + params[param];
        }
		
        return serializedParams;
    },

    updateElement: function(id, content) {        
        if(id == PrimeFaces.VIEW_STATE) {
            PrimeFaces.ajax.AjaxUtils.updateState.call(this, content);
        }
        else {
            $(PrimeFaces.escapeClientId(id)).replaceWith(content);

            // PrimeFaces Mobile
            if($.mobile) {
                var controls = $(PrimeFaces.escapeClientId(id)).parent().find("input, textarea, select, button, ul");

                // input and textarea
                controls
                    .filter("input, textarea")
                    .not("[type='radio'], [type='checkbox'], [type='button'], [type='submit'], [type='reset'], [type='image'], [type='hidden']")
                    .textinput();
                    
                // lists
                controls.filter("[data-role='listview']").listview();
                
                // buttons
                controls.filter("button, [type='button'], [type='submit'], [type='reset'], [type='image']" ).button();

                // slider
                controls.filter("input, select")
                        .filter("[data-role='slider'], [data-type='range']")
                        .slider();
                
                // selects
                controls.filter("select:not([data-role='slider'])" ).selectmenu();
            }
        }
    },

    /**
	 * Handles response handling tasks after updating the dom
	 */
    handleResponse: function(xmlDoc) {
        var redirect = xmlDoc.find('redirect'),
        callbackParams = xmlDoc.find('extension[ln="primefaces"][type="args"]'),
        pushData = xmlDoc.find('extension[ln="primefaces"][type="push-data"]'),
        scripts = xmlDoc.find('eval');

        if(redirect.length > 0) {
            window.location = redirect.attr('url');
        }
        else {
            // args
            this.args = callbackParams.length > 0 ? $.parseJSON(callbackParams.text()) : {};
            
            // push data
            this.pushData = pushData.length > 0 ? $.parseJSON(pushData.text()) : null;

            // scripts to execute
            for(var i=0; i < scripts.length; i++) {
                $.globalEval(scripts.eq(i).text());
            }
        }
        
        // Handle push data
        if(this.pushData) {
            for(var channel in this.pushData) {
                if(channel) {
                    var message = JSON.stringify(this.pushData[channel].data);

                    PrimeFaces.websockets[channel].send(message);
                }
            }
        }
    }
};

PrimeFaces.ajax.AjaxRequest = function(cfg, ext) {
    PrimeFaces.debug('Initiating ajax request.');
    
    if(cfg.onstart) {
       var retVal = cfg.onstart.call(this);
       if(retVal == false) {
           PrimeFaces.debug('Ajax request cancelled by onstart callback.');
           return;  // cancel request
       }
    }

    var form = null,
    sourceId = null;
    
    // source can be a client id or an element defined by this keyword
    if(typeof(cfg.source) == 'string') {
        sourceId = cfg.source;
    } else {
        sourceId = $(cfg.source).attr('id');
    }
    
    if(cfg.formId) {
        form = $(PrimeFaces.escapeClientId(cfg.formId));                    // Explicit
																			// form
																			// is
																			// defined
    }
    else {
        form = $(PrimeFaces.escapeClientId(sourceId)).parents('form:first');     // look
																					// for
																					// a
																					// parent
																					// of
																					// source

        // source has no parent form so use first form in document
        if(form.length == 0) {
            form = $('form').eq(0);
        }
    }
    
    PrimeFaces.debug('Form to post ' + form.attr('id') + '.');

    var postURL = form.attr('action'),
    postParams = form.serialize(),
    encodedURLfield = form.children("input[name='javax.faces.encodedURL']");

    // portlet support
    var pForms = null;
    if(encodedURLfield.length > 0) {
        postURL = encodedURLfield.val();
        pForms = $('form[action="' + form.attr('action') + '"]');   // find
																	// forms of
																	// the
																	// portlet
    }
    
    PrimeFaces.debug('URL to post ' + postURL + '.');

    // partial ajax
    postParams = postParams + "&" + PrimeFaces.PARTIAL_REQUEST_PARAM + "=true";

    // source
    postParams = postParams + "&" + PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + sourceId;

    // process
    var process = [];
    if(cfg.process)
        process.push(cfg.process);
    if(ext && ext.process)
        process.push(ext.process);
    
    if(process.length > 0)
        postParams = postParams + "&" + PrimeFaces.PARTIAL_PROCESS_PARAM + "=" + process.join(' ');

    // update
    var update = [];
    if(cfg.update)
        update.push(cfg.update);
    if(ext && ext.update)
        update.push(ext.update);
    
    if(update.length > 0)
        postParams = postParams + "&" + PrimeFaces.PARTIAL_UPDATE_PARAM + "=" + update.join(' ');

    // behavior event
    if(cfg.event) {
        postParams = postParams + "&" + PrimeFaces.BEHAVIOR_EVENT_PARAM + "=" + cfg.event;
        var domEvent = cfg.event;

        if(cfg.event == 'valueChange')
            domEvent = 'change';
        else if(cfg.event == 'action')
            domEvent = 'click';

        postParams = postParams + "&" + PrimeFaces.PARTIAL_EVENT_PARAM + "=" + domEvent;
    } else {
        postParams = postParams + "&" + cfg.source + "=" + cfg.source;
    }
    
    // params
    if(cfg.params) {
        postParams = postParams + PrimeFaces.ajax.AjaxUtils.serialize(cfg.params);
    }
    if(ext && ext.params) {
        postParams = postParams + PrimeFaces.ajax.AjaxUtils.serialize(ext.params);
    }
    
    PrimeFaces.debug('Post Data:' + postParams);
	
    var xhrOptions = {
        url : postURL,
        type : "POST",
        cache : false,
        dataType : "xml",
        data : postParams,
        portletForms: pForms,
        source: cfg.source,
        beforeSend: function(xhr) {
           xhr.setRequestHeader('Faces-Request', 'partial/ajax');
        },
        error: function(xhr, status, errorThrown) {
            if(cfg.onerror) {
                cfg.onerror.call(xhr, status, errorThrown);
            }
    
            PrimeFaces.error('Request return with error:' + status + '.');
        },
        success : function(data, status, xhr) {
            PrimeFaces.debug('Response received succesfully.');
            
            var parsed;

            // call user callback
            if(cfg.onsuccess) {
                parsed = cfg.onsuccess.call(this, data, status, xhr);
            }

            // extension callback that might parse response
            if(ext && ext.onsuccess && !parsed) {
                parsed = ext.onsuccess.call(this, data, status, xhr); 
            }

            // do not execute default handler as response already has been
			// parsed
            if(parsed) {
                return;
            } 
            else {
                PrimeFaces.ajax.AjaxResponse.call(this, data, status, xhr);
            }
            
            PrimeFaces.debug('DOM is updated.');
        },
        complete : function(xhr, status) {
            if(cfg.oncomplete) {
                cfg.oncomplete.call(this, xhr, status, this.args);
            }
            
            if(ext && ext.oncomplete) {
                ext.oncomplete.call(this, xhr, status, this.args);
            }
            
            PrimeFaces.debug('Response completed.');

            PrimeFaces.ajax.RequestManager.poll();
        }
    };
	
    xhrOptions.global = cfg.global == true || cfg.global == undefined ? true : false;

    if(cfg.async) {
        $.ajax(xhrOptions);
    } else {
        PrimeFaces.ajax.RequestManager.offer(xhrOptions);
    }
}

PrimeFaces.ajax.AjaxResponse = function(responseXML) {
    var xmlDoc = $(responseXML.documentElement),
    updates = xmlDoc.find('update');

    for(var i=0; i < updates.length; i++) {
        var update = updates.eq(i),
        id = update.attr('id'),
        content = update.text();

        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
    }

    PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
}

PrimeFaces.ajax.RequestManager = {
		
    requests : new Array(),

    offer : function(req) {
        this.requests.push(req);

        if(this.requests.length == 1) {
            var retVal = $.ajax(req);
            if(retVal === false)
                this.poll();
        }
    },

    poll : function() {
        if(this.isEmpty()) {
            return null;
        }
 
        var processedRequest = this.requests.shift();
        var nextRequest = this.peek();
        if(nextRequest != null) {
            $.ajax(nextRequest);
        }

        return processedRequest;
    },

    peek : function() {
        if(this.isEmpty()) {
            return null;
        }
    
        var nextRequest = this.requests[0];
  
        return nextRequest;
    },
    
    isEmpty : function() {
        return this.requests.length == 0;
    }
};

/**
 * Utilities
 */
Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length);
  this.length = from < 0 ? this.length + from : from;
  return this.push.apply(this, rest);
};

String.prototype.startsWith = function(str){
    return (this.indexOf(str) === 0);
}

/**
 * Prime Push Widget
 */
PrimeFaces.widget.PrimeWebSocket = function(cfg) {
    this.cfg = cfg;

    if(this.cfg.autoConnect) {
        this.connect();
    }
}

PrimeFaces.widget.PrimeWebSocket.prototype.send = function(data) {
    this.ws.send(data);
}

PrimeFaces.widget.PrimeWebSocket.prototype.connect = function() {
    this.ws = $.browser.mozilla ? new MozWebSocket(this.cfg.url) : new WebSocket(this.cfg.url);

    var _self = this;

    this.ws.onmessage = function(evt) {
        var pushData = $.parseJSON(evt.data);

        if(_self.cfg.onmessage) {
            _self.cfg.onmessage.call(_self, evt, pushData);
        }
    };

    this.ws.onclose = function() {
        
    };
    
    this.ws.onerror = function(evt) {
        alert(evt.data);
    };

    PrimeFaces.websockets[this.cfg.channel] = this;
}

PrimeFaces.widget.PrimeWebSocket.prototype.close = function() {
    this.ws.close();
}

/**
	 * PrimeFaces Accordion Widget
	 */
PrimeFaces.widget.AccordionPanel = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.cfg.id);
    this.jq = $(this.jqId);
    this.stateHolder = $(this.jqId + '_active');
    this.headers = this.jq.children('.ui-accordion-header');
    this.panels = this.jq.children('.ui-accordion-content');
    this.headers.children('a').disableSelection();
    this.onshowHandlers = [];
    
    // options
    this.cfg.active = this.cfg.multiple ? this.stateHolder.val().split(',') : this.stateHolder.val();
    
    this.bindEvents();
    
    if(this.cfg.dynamic && this.cfg.cache) {
        this.markAsLoaded(this.panels.eq(this.cfg.active));
    }
    
    this.jq.data('widget', this);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.AccordionPanel, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.AccordionPanel.prototype.bindEvents = function() {
    var _self = this;
    
    this.headers.mouseover(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active')&&!element.hasClass('ui-state-disabled')) {
            element.addClass('ui-state-hover');
        }
    }).mouseout(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active')&&!element.hasClass('ui-state-disabled')) {
            element.removeClass('ui-state-hover');
        }
    }).click(function(e) {
        var element = $(this);
        if(!element.hasClass('ui-state-disabled')) {
            var tabIndex = element.index() / 2;
            
            if(element.hasClass('ui-state-active')) {
                _self.unselect(tabIndex);
            }
            else {
                _self.select(tabIndex);
            }
        }
        
        e.preventDefault();
    });
}

/**
 * Activates a tab with given index
 */
PrimeFaces.widget.AccordionPanel.prototype.select = function(index) {
    var panel = this.panels.eq(index);
    
    // Call user onTabChange callback
    if(this.cfg.onTabChange) {
        var result = this.cfg.onTabChange.call(this, panel);
        if(result == false)
            return false;
    }
    
    var shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

    // update state
    if(this.cfg.multiple)
        this.addToSelection(index);
    else
        this.cfg.active = index;
    
    this.saveState();
    
    if(shouldLoad) {
        this.loadDynamicTab(panel);
    }
    else {
        this.show(panel);
        this.fireTabChangeEvent(panel);
    }
    
    return true;
}

/**
 * Deactivates a tab with given index
 */
PrimeFaces.widget.AccordionPanel.prototype.unselect = function(index) {
    var panel = this.panels.eq(index),
    header = panel.prev();
    
    header.children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
    header.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all');
    panel.slideUp();
    
    this.removeFromSelection(index);
    this.saveState();
}

PrimeFaces.widget.AccordionPanel.prototype.show = function(panel) {
    var _self = this;
    
    // deactivate current
    if(!this.cfg.multiple) {
        var oldHeader = this.headers.filter('.ui-state-active');
        oldHeader.children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
        oldHeader.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all').next().slideUp();
    }
    
    // activate selected
    var newHeader = panel.prev();
    newHeader.addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
             .children('.ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');
             
    panel.slideDown('normal', function() {
        _self.postTabShow(panel);
    });
}

/**
 * Loads tab contents with ajax
 */
PrimeFaces.widget.AccordionPanel.prototype.loadDynamicTab = function(panel) {
    var _self = this,
    options = {
        source: this.id,
        process: this.id,
        update: this.id
    };

    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                $(panel).html(content);

                if(_self.cfg.cache) {
                    _self.markAsLoaded(panel);
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };
    
    options.oncomplete = function() {
        _self.show(panel);
    };

    var params = {};
    params[this.id + '_contentLoad'] = true;
    params[this.id + '_newTab'] = panel.attr('id');
    params[this.id + '_tabindex'] = parseInt(panel.index() / 2);

    options.params = params;
    
    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'];
        
        tabChangeBehavior.call(this, null, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

/**
 * Fires an ajax tabChangeEvent if a tabChangeListener is defined on server side
 */
PrimeFaces.widget.AccordionPanel.prototype.fireTabChangeEvent = function(panel) {
    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_newTab'] = panel.attr('id');
        ext.params[this.id + '_tabindex'] = parseInt(panel.index() / 2);


        tabChangeBehavior.call(this, null, ext);
    }
}

PrimeFaces.widget.AccordionPanel.prototype.markAsLoaded = function(panel) {
    panel.data('loaded', true);
}

PrimeFaces.widget.AccordionPanel.prototype.isLoaded = function(panel) {
    return panel.data('loaded') == true;
}

PrimeFaces.widget.AccordionPanel.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

PrimeFaces.widget.AccordionPanel.prototype.addToSelection = function(nodeId) {
    this.cfg.active.push(nodeId);
}

PrimeFaces.widget.AccordionPanel.prototype.removeFromSelection = function(nodeId) {
    this.cfg.active = $.grep(this.cfg.active, function(r) {
        return r != nodeId;
    });
}

PrimeFaces.widget.AccordionPanel.prototype.saveState = function() {
    if(this.cfg.multiple)
        this.stateHolder.val(this.cfg.active.join(','));
    else
        this.stateHolder.val(this.cfg.active);
}

PrimeFaces.widget.AccordionPanel.prototype.addOnshowHandler = function(fn) {
    this.onshowHandlers.push(fn);
}

PrimeFaces.widget.AccordionPanel.prototype.postTabShow = function(newPanel) {            
    // Call user onTabShow callback
    if(this.cfg.onTabShow) {
        this.cfg.onTabShow.call(this, newPanel);
    }
    
    // execute onshowHandlers and remove successful ones
    this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
		return !fn.call();
	});
} 

PrimeFaces.widget.AjaxStatus = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.AjaxStatus, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.AjaxStatus.prototype.bindFacet = function(eventName, facetToShow) {
    var _self = this;
    
	$(document).bind(eventName, function() {
		$(_self.jqId).children().hide();
	
		$(_self.jqId + '_' + facetToShow).show();
	});
}

PrimeFaces.widget.AjaxStatus.prototype.bindCallback = function(eventName, fn) {
	$(document).bind(eventName, fn);
}

/**
	 * PrimeFaces AutoComplete Widget
	 */
PrimeFaces.widget.AutoComplete = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.panelId = this.jqId + '_panel';
    this.input = $(this.jqId + '_input');
    this.hinput = $(this.jqId + '_hinput');
    this.panel = this.jq.children(this.panelId);
    this.dropdown = this.jq.children('.ui-button');
    this.disabled = this.input.is(':disabled');
    this.active = true;
    this.cfg.pojo = this.hinput.length == 1;
    
    // options
    this.cfg.minLength = this.cfg.minLength != undefined ? this.cfg.minLength : 1;
    this.cfg.delay = this.cfg.delay != undefined ? this.cfg.delay : 300;
    
    // visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.input);
    }
    
    // core events
    this.bindStaticEvents();
    
    // client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    // force selection
    if(this.cfg.forceSelection) {
        this.setupForceSelection();
    }
    
    // Panel management
    $(document.body).children(this.panelId).remove();
    this.panel.appendTo(document.body);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.AutoComplete, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.AutoComplete.prototype.bindStaticEvents = function() {
    var _self = this;
    
    // bind keyup handler
    this.input.keyup(function(e) {
        var keyCode = $.ui.keyCode,
        key = e.which,
        shouldSearch = true;
                
        if(key == keyCode.UP 
            || key == keyCode.LEFT 
            || key == keyCode.DOWN 
            || key == keyCode.RIGHT 
            || key == keyCode.TAB 
            || key == keyCode.ENTER
            || key == keyCode.NUMPAD_ENTER) {
            shouldSearch = false;
        } 
        else if(_self.cfg.pojo) {
            _self.hinput.val($(this).val());
        }
        
        if(shouldSearch) {
            var value = _self.input.val();
        
            if(value.length >= _self.cfg.minLength) {

                // Cancel the search request if user types within the timeout
                if(_self.timeout) {
                    clearTimeout(_self.timeout);
                }

                _self.timeout = setTimeout(function() {
                    _self.search(value);
                }, 
                _self.cfg.delay);
            }
        }
 
    });
  
    // key events
    this.input.keydown(function(e) {
        if(_self.panel.is(':visible')) {
            var keyCode = $.ui.keyCode,
            currentItems = _self.panel.find('.ui-autocomplete-item'),
            highlightItem = _self.panel.find('.ui-autocomplete-item.ui-state-highlight');
        
            switch(e.which) {
                case keyCode.UP:
                case keyCode.LEFT:
                    var prev;
                    if(highlightItem.length > 0) {
                        prev = highlightItem.removeClass('ui-state-highlight').prev();
                        if(prev.length > 0){
                            prev.addClass('ui-state-highlight');
                            var diff = prev.offset().top - _self.panel.offset().top - prev.outerHeight(true) + prev.height();
                            if( diff < 0 )
                                _self.panel.scrollTop( _self.panel.scrollTop() + diff);
                        }
                    } 
                    
                    if(!prev || prev.length == 0) {
                        prev = currentItems.eq(currentItems.length - 1).addClass('ui-state-highlight');
                        _self.panel.scrollTop(prev.offset().top + prev.outerHeight(true) - _self.panel.offset().top - _self.panel.height());
                    }

                    e.preventDefault();
                    break;
                    
                case keyCode.DOWN:
                case keyCode.RIGHT:
                    var next;
                    if(highlightItem.length > 0) {
                        next = highlightItem.removeClass('ui-state-highlight').next();
                        if(next.length > 0){
                            next.addClass('ui-state-highlight');
                            var diff = next.offset().top + next.outerHeight(true) - _self.panel.offset().top;
                            if( diff > _self.panel.height() )
                                _self.panel.scrollTop(_self.panel.scrollTop() + (diff - _self.panel.height()));
                       }
                    } 
                    
                    if(!next || next.length == 0) {
                        currentItems.eq(0).addClass('ui-state-highlight');
                        _self.panel.scrollTop(0);
                    }

                    e.preventDefault();
                    break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    highlightItem.click();

                    e.preventDefault();
                    break;

                case keyCode.ALT: 
                case 224:
                    break;

                case keyCode.TAB:
                    _self.hide();
                    break;
            }
        }
         
    });
    
    // dropdown
    this.dropdown.mouseover(function() {
        if(!_self.disabled) {
            $(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        if(!_self.disabled) {
            $(this).removeClass('ui-state-hover');
        }
    }).mousedown(function() {
        if(!_self.disabled && _self.active) {
            $(this).addClass('ui-state-active');
        }
    }).mouseup(function() {
        if(!_self.disabled && _self.active) {
            $(this).removeClass('ui-state-active');
            
            _self.search('');
            _self.input.focus();
        }
    });

    // hide overlay when outside is clicked
    var offset;
    $(document.body).bind('click', function (e) {
        if(_self.panel.is(":hidden")) {
            return;
        }
        offset = _self.panel.offset();
        if(e.target === _self.input.get(0)) {
            return;
        }
        if (e.pageX < offset.left ||
            e.pageX > offset.left + _self.panel.width() ||
            e.pageY < offset.top ||
            e.pageY > offset.top + _self.panel.height()) {
            _self.hide();
        }
        _self.hide();
    });
}

PrimeFaces.widget.AutoComplete.prototype.bindDynamicEvents = function() {
    var _self = this,
    items = this.panel.find('.ui-autocomplete-item');
    
    // visuals and click handler for items
    items.bind('mouseover', function() {
        $(this).addClass('ui-state-highlight');
    })
    .bind('mouseout', function() {
        $(this).removeClass('ui-state-highlight');
    })
    .bind('click', function(event) {
        var item = $(this);
        
        _self.input.val(item.attr('data-item-label'));
        
        if(_self.cfg.pojo) {
            _self.hinput.val(item.attr('data-item-value'));            
        } 

        _self.invokeItemSelectBehavior(event);
    });
}

PrimeFaces.widget.AutoComplete.prototype.search = function(value) {
    if(!this.active) {
        return;
    }
    
    var _self = this;
    
    // start callback
    if(this.cfg.onstart) {
        this.cfg.onstart.call(this, value);
    }

    var options = {
        source: this.id,
        process: this.id,
        update: this.id,
        formId: this.cfg.formId,
        onsuccess: function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                data = update.text();

                if(id == _self.id) {
                    _self.panel.html(data);
                    _self.bindDynamicEvents();
                    
                    var items = _self.panel.find('.ui-autocomplete-item');
                    
                    if(items.length > 0) {
                        // highlight first item
                        items.eq(0).addClass('ui-state-highlight');

                        // highlight query string
                        if(_self.panel.children().is('ul')) {
                            items.each(function() {
                                var item = $(this);
                                item.html(item.text().replace(value, '<span class="ui-autocomplete-query">' + value + '</span>'));
                            });
                        }
                        
                        if(_self.cfg.forceSelection) {
                            _self.cachedResults = [];
                            items.each(function(i, item) {
                                _self.cachedResults.push($(item).attr('data-item-label'));
                            });
                        }
                        
                        if(_self.panel.is(':hidden')) {
                            _self.show();
                        }

                        // adjust height
                        _self.panel.css('height', '');
                        if(_self.cfg.scrollHeight && _self.panel.height() > _self.cfg.scrollHeight) {
                            _self.panel.css('height', _self.cfg.scrollHeight + 'px');
                        }
                    }
                    else {
                        _self.panel.hide();
                    }
                } 
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, data);
                }
            }
            
            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        }
    };
    
    // complete callback
    if(this.cfg.oncomplete) {
        options.oncomplete = this.cfg.oncomplete;
    }

    if(this.cfg.global === false) {
        options.global = false;
    }

    var params = {};
    params[this.id + '_query'] = encodeURIComponent(value);

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.AutoComplete.prototype.show = function() {
    this.alignPanel();
    
    this.panel.css('z-index', ++PrimeFaces.zindex);
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
    }

    if(this.cfg.effect)
        this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
    else
        this.panel.show();
}

PrimeFaces.widget.AutoComplete.prototype.hide = function() {
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '');
    }
    
    this.panel.css('z-index', '').hide();
}

PrimeFaces.widget.AutoComplete.prototype.invokeItemSelectBehavior = function(event) {
    if(this.cfg.behaviors) {
        var itemSelectBehavior = this.cfg.behaviors['itemSelect'];

        if(itemSelectBehavior) {
            itemSelectBehavior.call(this, event);
        }
    }
}

PrimeFaces.widget.AutoComplete.prototype.setupForceSelection = function() {
    this.cachedResults = [this.input.val()];
    var _self = this;

    this.input.blur(function() {
        var value = $(this).val(),
        valid = false;

        for(var i = 0; i < _self.cachedResults.length; i++) {
            if(_self.cachedResults[i] == value) {
                valid = true;
                break;
            }
        }

        if(!valid) {
            $(this).val('');
        }
    });
}

PrimeFaces.widget.AutoComplete.prototype.disable = function() {
    this.disabled = true;
    this.input.addClass('ui-state-disabled').attr('disabled', 'disabled');
}

PrimeFaces.widget.AutoComplete.prototype.enable = function() {
    this.disabled = false;
    this.input.removeClass('ui-state-disabled').removeAttr('disabled');
}

PrimeFaces.widget.AutoComplete.prototype.close = function() {
    this.hide();
}

PrimeFaces.widget.AutoComplete.prototype.deactivate = function() {
    this.active = false;
}

PrimeFaces.widget.AutoComplete.prototype.activate = function() {
    this.active = true;
}

PrimeFaces.widget.AutoComplete.prototype.alignPanel = function() {
    this.panel.css({
                    left:'',
                    top:'',
                    'width': this.input.innerWidth() + 'px'
              })
              .position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.input
              });
}

/**
	 * PrimeFaces Calendar Widget
	 */
PrimeFaces.widget.Calendar = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jq);
    this.input = $(this.jqId + '_input');
    this.jqEl = this.cfg.popup ? this.input : $(this.jqId + '_inline');
    
    // i18n and l7n
    this.configureLocale();

    // Select listener
    this.bindDateSelectListener();
    
    // weekends
    if(this.cfg.disabledWeekends) {
        this.cfg.beforeShowDay = $.datepicker.noWeekends;
    }

    // Setup timepicker
    var hasTimePicker = this.hasTimePicker();
    if(hasTimePicker) {
        this.configureTimePicker();
    }
    
    // Client behaviors, input skinning and z-index
    if(this.cfg.popup) {
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jqEl, this.cfg.behaviors);
        }

        if(this.cfg.theme != false) {
            PrimeFaces.skinInput(this.jqEl);
        }
        
        this.cfg.beforeShow = function() {
            setTimeout(function() {
                $('#ui-datepicker-div').css('z-index', ++PrimeFaces.zindex);
            }, 250);
        };
    }

    // image title
    this.cfg.buttonText = this.jqEl.attr('title') || '';

	// Initialize calendar
    if(!this.cfg.disabled) {
        if(hasTimePicker) {
            if(this.cfg.timeOnly)
                this.jqEl.timepicker(this.cfg);
            else
                this.jqEl.datetimepicker(this.cfg);
        }
        else {
            this.jqEl.datepicker(this.cfg);
        }
    }

    // button title
    this.jqEl.siblings('.ui-datepicker-trigger:button').attr('title', this.cfg.buttonText);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Calendar, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Calendar.prototype.configureLocale = function() {
    var localeSettings = PrimeFaces.locales[this.cfg.locale];
	
    if(localeSettings) {
        for(var setting in localeSettings) {
            this.cfg[setting] = localeSettings[setting];
        }
    }
}

PrimeFaces.widget.Calendar.prototype.bindDateSelectListener = function() {
    var _self = this;

    this.cfg.onSelect = function() {
        if(_self.cfg.popup) {
            _self.fireDateSelectEvent();
        }
        else {
            var newDate = $.datepicker.formatDate(_self.cfg.dateFormat, _self.getDate()),
            oldDate = _self.input.val();

            if(oldDate == newDate) {
                _self.setDate(null);
                _self.input.val('');
            }
            else {
                _self.input.val(newDate);
                _self.fireDateSelectEvent();
            }
        }
    };
}

PrimeFaces.widget.Calendar.prototype.fireDateSelectEvent = function() {
    if(this.cfg.behaviors) {
        var dateSelectBehavior = this.cfg.behaviors['dateSelect'];

        if(dateSelectBehavior) {
            dateSelectBehavior.call(this);
        }
    }
}

PrimeFaces.widget.Calendar.prototype.configureTimePicker = function() {
    var pattern = this.cfg.dateFormat,
    timeSeparatorIndex = pattern.indexOf('h');
    
    this.cfg.dateFormat = pattern.substring(0, timeSeparatorIndex - 1);
    this.cfg.timeFormat = pattern.substring(timeSeparatorIndex, pattern.length);

    // second
    if(this.cfg.timeFormat.indexOf('ss') != -1) {
        this.cfg.showSecond = true;
    }

    // ampm
    if(this.cfg.timeFormat.indexOf('TT') != -1) {
        this.cfg.ampm = true;
    }
}

PrimeFaces.widget.Calendar.prototype.hasTimePicker = function() {
    return this.cfg.dateFormat.indexOf('h') != -1;
}

PrimeFaces.widget.Calendar.prototype.setDate = function(date) {
    this.jqEl.datetimepicker('setDate', date);
}

PrimeFaces.widget.Calendar.prototype.getDate = function() {
    return this.jqEl.datetimepicker('getDate');
}

PrimeFaces.widget.Calendar.prototype.enable = function() {
    this.jqEl.datetimepicker('enable');
}

PrimeFaces.widget.Calendar.prototype.disable = function() {
    this.jqEl.datetimepicker('disable');
}

/**
	 * PrimeFaces Carousel Widget
	 */
PrimeFaces.widget.Carousel = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.viewport = this.jq.children('.ui-carousel-viewport');
    this.header = this.jq.children('.ui-carousel-header'),
    this.list = this.viewport.children('ul');
    this.items = this.list.children('.ui-carousel-item');
    this.prevButton = this.header.children('.ui-carousel-prev-button');
    this.nextButton = this.header.children('.ui-carousel-next-button');
    this.pageLinks = this.header.find('.ui-carousel-page-links .ui-carousel-page-link');
    this.dropdown = this.header.children('.ui-carousel-dropdown');
    this.state = $(this.jqId + '_first');
    
    // configuration
    this.cfg.numVisible = this.cfg.numVisible||3;
    this.cfg.pageLinks = this.cfg.pageLinks||3;
    this.cfg.effect = this.cfg.effect||'slide';
    this.cfg.effectDuration = this.cfg.effectDuration||500;
    this.cfg.easing = this.cfg.easing||'easeInOutCirc';
    this.cfg.pageCount = Math.ceil(this.items.length / this.cfg.numVisible);
    this.cfg.firstVisible = (this.cfg.firstVisible||0) % this.items.length;
    this.cfg.page = (this.cfg.firstVisible / this.cfg.numVisible) + 1;
    this.animating = false;
    
    var firstItem = this.items.filter(':first').get(0);
    this.cfg.itemOuterWidth = parseInt(this.getProperty(firstItem, 'width')) + parseInt(this.getProperty(firstItem, 'margin-Left')) + parseInt(this.getProperty(firstItem, 'margin-Right')) +  ((parseInt(this.getProperty(firstItem, 'border-Left-Width')) + parseInt(this.getProperty(firstItem, 'border-Right-Width'))));
    this.cfg.itemOuterHeight = parseInt(this.getProperty(firstItem, 'height')) + Math.max(parseInt(this.getProperty(firstItem, 'margin-Top')), parseInt(this.getProperty(firstItem, 'margin-Bottom'))) + ((parseInt(this.getProperty(firstItem, 'border-Top-Width')) + parseInt(this.getProperty(firstItem, 'border-Bottom-Width'))));

    // viewport width/height
    if(this.cfg.vertical) {
        this.viewport.width(this.cfg.itemOuterWidth);
        this.viewport.height(this.cfg.numVisible * this.cfg.itemOuterHeight);
    }
    else{
        this.viewport.width(this.cfg.numVisible * this.cfg.itemOuterWidth);
        this.viewport.height(this.cfg.itemOuterHeight);
    }
    this.jq.width(this.viewport.outerWidth(true));
  
    // set offset position
    this.setOffset(this.getItemPosition(this.cfg.firstVisible));

    this.checkButtons();

    this.bindEvents();
    
    this.jq.css({
        visibility: 'visible'
    });
    
    if(this.cfg.autoPlayInterval) {
        this.startAutoPlay();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Carousel, PrimeFaces.widget.BaseWidget);

/**
 * Returns browser specific computed style property value.
 */
PrimeFaces.widget.Carousel.prototype.getProperty = function(item, prop){
    return $.browser.msie ? item.currentStyle.getAttribute(prop.replace(/-/g, "")) : document.defaultView.getComputedStyle(item, "").getPropertyValue(prop.toLowerCase());
}

/**
 * Autoplay startup.
 */
PrimeFaces.widget.Carousel.prototype.startAutoPlay = function(){
    var _self = this;
    if(this.cfg.autoPlayInterval){
        setInterval(function() {
            _self.next();
        }, this.cfg.autoPlayInterval);
    }
}

/**
 * Binds related mouse/key events.
 */
PrimeFaces.widget.Carousel.prototype.bindEvents = function(){
    var _self = this;
  
    this.pageLinks.click(function(e) {
        if(!_self.animating) {
            _self.setPage($(this).index() + 1);
        }
        
        e.preventDefault();
    });
    
    this.dropdown.change(function(e) {
        if(!_self.animating)
            _self.setPage(parseInt($(this).val()));
    });
  
    this.prevButton.click(function(e) {
        if(!_self.prevButton.hasClass('ui-state-disabled') && !_self.animating)
            _self.prev();
    });
  
    this.nextButton.click(function(){
        if(!_self.nextButton.hasClass('ui-state-disabled') && !_self.animating)
            _self.next();
    });
}

/**
 * Calculates position of list for a page index.
 */
PrimeFaces.widget.Carousel.prototype.getPagePosition = function(index) {
    return -((index - 1) * (this.cfg.vertical ? this.cfg.itemOuterHeight : this.cfg.itemOuterWidth) * this.cfg.numVisible);
}

/**
 * Calculates position of a given indexed item.
 */
PrimeFaces.widget.Carousel.prototype.getItemPosition = function(index){
    return -(index * (this.cfg.vertical ? this.cfg.itemOuterHeight : this.cfg.itemOuterWidth));
}

/**
 * Returns instant position of list.
 */
PrimeFaces.widget.Carousel.prototype.getPosition = function(){
    return parseInt(this.list.css(this.cfg.vertical ? 'top' : 'left'));
};

/**
 * Sets instant position of list.
 */
PrimeFaces.widget.Carousel.prototype.setOffset = function(val) {
    this.list.css(this.cfg.vertical ? {
        'top' : val
    } : {
        'left' : val
    });
};

/**
 * Fade animation for list transition.
 */
PrimeFaces.widget.Carousel.prototype.fade = function(val){
    var _self = this;
    this.list.animate(
    {
        opacity: 0
    }, 
    {
        duration: this.cfg.effectDuration / 2,
        specialEasing: {
            opacity : this.cfg.easing
        },
        complete: function() {
            _self.setOffset(val);
            $(this).animate( 
            {
                opacity: 1
            }, 
            {
                duration: _self.cfg.effectDuration / 2,
                specialEasing: {
                    opacity : _self.cfg.easing
                },
                complete: function() {
                    _self.animating = false;
                }
            });
        }
    });
}

PrimeFaces.widget.Carousel.prototype.slide = function(val){
    var _self = this,
    animateOption = this.cfg.vertical ? {
        top : val
    } : {
        left : val
    };
  
    this.list.animate( 
        animateOption, 
        {
            duration: this.cfg.effectDuration,
            easing: this.cfg.easing,
            complete: function() {
                _self.animating = false;
            }
        });
}

/**
 * Go next page
 */
PrimeFaces.widget.Carousel.prototype.next = function(){
   this.setPage(this.cfg.page + 1);
}

/**
 * Go previous page
 */
PrimeFaces.widget.Carousel.prototype.prev = function(){
   this.setPage(this.cfg.page - 1);
}

/**
 * Navigation to a given page index.
 */
PrimeFaces.widget.Carousel.prototype.setPage = function(index) {    
    if(this.cfg.isCircular)
        this.cfg.page = index > this.cfg.pageCount ? 1 : index < 1 ? this.cfg.pageCount : index;
    else
        this.cfg.page  = index;
  
    this.checkButtons();
    
    this.state.val((this.cfg.page - 1) * this.cfg.numVisible);
    
    var newPosition = this.getPagePosition(this.cfg.page);
  
    if(this.getPosition() == newPosition) {
        this.animating = false;
        return;
    }
    
    if(this.cfg.effect == 'fade')
        this.fade(newPosition);
    else
        this.slide(newPosition);
}

/**
 * Enables/Disables navigation controls
 */
PrimeFaces.widget.Carousel.prototype.checkButtons = function() {
    this.pageLinks.filter('.ui-icon-radio-on').removeClass('ui-icon-radio-on');
    this.pageLinks.eq(this.cfg.page - 1).addClass('ui-icon-radio-on');
    
    this.dropdown.val(this.cfg.page);
  
    // no bound
    if(this.cfg.isCircular)
        return;
  
    // lower bound
    if(this.cfg.page == 1){
        this.prevButton.addClass('ui-state-disabled');
    }
    else{
        this.prevButton.removeClass('ui-state-disabled');
    }
  
    // upper bound
    if(this.cfg.page >= this.cfg.pageCount){
        this.nextButton.addClass('ui-state-disabled');
    }
    else{
        this.nextButton.removeClass('ui-state-disabled');
    }
};

/**
	 * PrimeFaces Dashboard Widget
	 */
PrimeFaces.widget.Dashboard = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
	this.cfg.connectWith = this.COLUMN_CLASS;
	this.cfg.placeholder = this.PLACEHOLDER_CLASS;
	this.cfg.forcePlaceholderSize = true;
	this.cfg.revert=true;
    this.cfg.handle='.ui-panel-titlebar';

    var _self = this;
	
    if(this.cfg.behaviors) {
        var reorderBehavior = this.cfg.behaviors['reorder'];
        
        if(reorderBehavior) {
            this.cfg.update = function(e, ui) {
                
                if(this === ui.item.parent()[0]) {
                    var itemIndex = ui.item.parent().children().filter(':not(script):visible').index(ui.item),
                    receiverColumnIndex =  ui.item.parent().parent().children().index(ui.item.parent());

                    var ext = {
                        params: {}
                    }  
                    ext.params[_self.id + "_reordered"] = true;
                    ext.params[_self.id + "_widgetId"] = ui.item.attr('id');
                    ext.params[_self.id + "_itemIndex"] = itemIndex;
                    ext.params[_self.id + "_receiverColumnIndex"] = receiverColumnIndex;

                    if(ui.sender) {
                        ext.params[_self.id + "_senderColumnIndex"] = ui.sender.parent().children().index(ui.sender);
                    }

                    reorderBehavior.call(_self, e, ext);
                }
                
            };
        }
    } 
	
	$(this.jqId + " " + this.COLUMN_CLASS).sortable(this.cfg);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Dashboard, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Dashboard.prototype.COLUMN_CLASS = '.ui-dashboard-column';

PrimeFaces.widget.Dashboard.prototype.PLACEHOLDER_CLASS = 'ui-state-hover';
/**
 * PrimeFaces DataGrid Widget
 */
PrimeFaces.widget.DataGrid = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.cfg.formId = $(this.jqId).parents('form:first').attr('id');
    this.content = this.jqId + '_content';
	
    if(this.cfg.paginator) {
        this.setupPaginator();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.DataGrid, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.DataGrid.prototype.setupPaginator = function() {
    var _self = this;
    this.cfg.paginator.paginate = function(newState) {
        _self.handlePagination(newState);
    };

    this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
}

PrimeFaces.widget.DataGrid.prototype.handlePagination = function(newState) {
    
    var _self = this,
    options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId,
        onsuccess: function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    $(_self.content).html(content);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }
            
            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        }
    };

    var params = {};
    params[this.id + "_ajaxPaging"] = true;
    params[this.id + "_first"] = newState.first;
    params[this.id + "_rows"] = newState.rows;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.DataGrid.prototype.getPaginator = function() {
    return this.paginator;
}

/**
	 * PrimeFaces DataList Widget
	 */
PrimeFaces.widget.DataList = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.cfg.formId = $(this.jqId).parents('form:first').attr('id');
    this.content = this.jqId + '_content';

    if(this.cfg.paginator) {
        this.setupPaginator();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.DataList, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.DataList.prototype.setupPaginator = function() {
    var _self = this;
    this.cfg.paginator.paginate = function(newState) {
        _self.handlePagination(newState);
    };

    this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
}

PrimeFaces.widget.DataList.prototype.handlePagination = function(newState) {

    var _self = this,
    options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId,
        onsuccess: function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    $(_self.content).html(content);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }
            
            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        }
    };

    var params = {};
    params[this.id + "_ajaxPaging"] = true;
    params[this.id + "_first"] = newState.first;
    params[this.id + "_rows"] = newState.rows;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.DataList.prototype.getPaginator = function() {
    return this.paginator;
}

/**
	 * PrimeFaces DataTable Widget
	 */
PrimeFaces.widget.DataTable = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.tbody = this.jqId + '_data';
    this.tbodyId = this.jqId + '_data';
    this.cfg.formId = this.jq.parents('form:first').attr('id');

    // Paginator
    if(this.cfg.paginator) {
        this.setupPaginator();
    }

    // Sort events
    this.setupSortEvents();

    // Selection events
    if(this.cfg.selectionMode || this.cfg.columnSelectionMode) {
        this.selectionHolder = this.jqId + '_selection';

        var preselection = $(this.selectionHolder).val();
        this.selection = preselection == "" ? [] : preselection.split(',');

        this.setupSelectionEvents();
    }
    
    // Filtering
    if(this.cfg.filtering) {
        this.setupFiltering();
    }

    if(this.cfg.expansion) {
        this.expansionProcess = [];
        this.setupExpansionEvents();
    }

    var rowEditors = this.getRowEditors();
    if(rowEditors.length > 0) {
        this.setupCellEditorEvents(rowEditors);
    }
    
    if(this.cfg.scrollable||this.cfg.resizableColumns) {
        this.alignColumnWidths();
    }
    
    if(this.cfg.scrollable) {
        this.setupScrolling();
    }

    if(this.cfg.resizableColumns) {
        this.setupResizableColumns();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.DataTable, PrimeFaces.widget.BaseWidget);

/**
 * Binds the change event listener and renders the paginator
 */
PrimeFaces.widget.DataTable.prototype.setupPaginator = function() {
    var _self = this;
    this.cfg.paginator.paginate = function(newState) {
        _self.paginate(newState);
    };

    this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
}

/**
 * Applies events related to sorting in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupSortEvents = function() {
    var _self = this;
    
    $(this.jqId + ' th.ui-sortable-column').
        mouseover(function(){
            $(this).toggleClass('ui-state-hover');
        })
        .mouseout(function(){
            $(this).toggleClass('ui-state-hover');}
        )
        .click(function(event) {
            
            // Stop event if target is a clickable element inside header
            if($(event.target).is(':not(th,span,.ui-dt-c)')) {
                return;
            }
            
            PrimeFaces.clearSelection();

            var columnId = $(this).attr('id');
            
            // Reset previous sorted columns
            $(this).siblings().removeClass('ui-state-active').
                find('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');

            // Update sort state
            $(this).addClass('ui-state-active');
            var sortIcon = $(this).find('.ui-sortable-column-icon');
            
            if(sortIcon.hasClass('ui-icon-triangle-1-n')) {
                sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');

                _self.sort(columnId, "DESCENDING");
                PrimeFaces.clearSelection();
            }
            else if(sortIcon.hasClass('ui-icon-triangle-1-s')) {
                sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, "ASCENDING");
            } 
            else {
                sortIcon.addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, "ASCENDING");
            }
        });
}

/**
 * Binds filter events to filters
 */
PrimeFaces.widget.DataTable.prototype.setupFiltering = function() {
    var _self = this;
    
    $(this.jqId + ' thead:first th.ui-filter-column .ui-dt-c .ui-column-filter').each(function(index) {
        var filter = $(this);
        if(filter.is('input:text')) {
            filter.keyup(function(e) {
                if(_self.cfg.filterEvent == 'keyup' || (_self.cfg.filterEvent == 'enter' && e.which == $.ui.keyCode.ENTER)){
                    _self.filter(e);
                    
                    e.preventDefault();
                }
            });
        } 
        else {
            filter.change(function(e) {
                _self.filter(e);
            });
        }
    });
}

/**
 * Applies events related to selection in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupSelectionEvents = function() {
    var _self = this;

    // Row mouseover, mouseout, click
    if(this.cfg.selectionMode) {
        var selectEvent = this.cfg.dblclickSelect ? 'dblclick' : 'click';

        $(this.jqId + ' tbody.ui-datatable-data > tr.ui-widget-content').css('cursor', 'pointer')
            .die('mouseover.datatable mouseout.datatable contextmenu.datatable ' + selectEvent + '.datatable')
            .live('mouseover.datatable', function() {
                var element = $(this);

                if(!element.hasClass('ui-state-highlight')) {
                    element.addClass('ui-state-hover');
                }

            })
            .live('mouseout.datatable', function() {
                var element = $(this);

                if(!element.hasClass('ui-state-highlight')) {
                    element.removeClass('ui-state-hover');
                }
            })
            .live(selectEvent + '.datatable', function(event) {
                _self.onRowClick(event, this);
            })
            .live('contextmenu.datatable', function(event) {
               _self.onRowClick(event, this);
               event.preventDefault();
            });
    }
    // Radio-Checkbox based rowselection
    else if(this.cfg.columnSelectionMode) {
        
        if(this.cfg.columnSelectionMode == 'single') {
            var radios = $(this.jqId + ' tbody.ui-datatable-data td.ui-selection-column .ui-radiobutton .ui-radiobutton-box');
            
            radios.die('click').live('click', function() {
                var radio = $(this),
                checked = radio.hasClass('ui-state-active'),
                disabled = radio.hasClass('ui-state-disabled');
                
                if(!disabled && !checked) {
                    // unselect previous
                    radios.filter('.ui-state-active').removeClass('ui-state-active')
                           .children('span.ui-radiobutton-icon').removeClass('ui-icon ui-icon-bullet');

                    // select current
                    radio.addClass('ui-state-active').children('.ui-radiobutton-icon').addClass('ui-icon ui-icon-bullet');

                    _self.selectRowWithRadio(radio.parents('tr:first'));
                }
            }).die('mouseover').live('mouseover', function() {
                var radio = $(this);
                if(!radio.hasClass('ui-state-disabled')&&!radio.hasClass('ui-state-active')) {
                    radio.addClass('ui-state-hover');
                }
            }).die('mouseout').live('mouseout', function() {
                var radio = $(this);
                radio.removeClass('ui-state-hover');
            });
        }
        else {
            this.checkAllToggler = $(this.jqId + ' table thead th.ui-selection-column .ui-chkbox.ui-chkbox-all .ui-chkbox-box');
            
            // check-uncheck all
            this.checkAllToggler.die('mouseover').live('mouseover', function() {
                var box = $(this);
                if(!box.hasClass('ui-state-disabled')&&!box.hasClass('ui-state-active')) {
                    box.addClass('ui-state-hover');
                }
            }).die('mouseout').live('mouseout', function() {
                $(this).removeClass('ui-state-hover');
            }).die('click').live('click', function() {
                _self.toggleCheckAll();
            });
            
            // row checkboxes
            $(this.jqId + ' tbody.ui-datatable-data td.ui-selection-column .ui-chkbox .ui-chkbox-box').die('mouseover').live('mouseover', function() {
                var box = $(this);
                if(!box.hasClass('ui-state-disabled')&&!box.hasClass('ui-state-active')) {
                    box.addClass('ui-state-hover');
                }
            }).die('mouseout').live('mouseout', function() {
                $(this).removeClass('ui-state-hover');
            }).die('click').live('click', function() {
                var box = $(this);
                
                if(!box.hasClass('ui-state-disabled')) {
                    var checked = box.hasClass('ui-state-active');

                    if(checked) {
                        _self.unselectRowWithCheckbox(box);
                    } 
                    else {                        
                        _self.selectRowWithCheckbox(box);
                    }
                }
            });
        }
    }
}

/**
 * Applies events related to row expansion in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupExpansionEvents = function() {
    var _self = this;

    $(this.jqId + ' tbody.ui-datatable-data tr td span.ui-row-toggler')
            .die()
            .live('click', function() {
                _self.toggleExpansion(this);
            });
}

/**
 * Initialize data scrolling, for live scrolling listens scroll event to load
 * data dynamically
 */
PrimeFaces.widget.DataTable.prototype.setupScrolling = function() {
    var scrollHeader = $(this.jqId + ' .ui-datatable-scrollable-header'),
    scrollBody = $(this.jqId + ' .ui-datatable-scrollable-body'),
    scrollFooter = $(this.jqId + ' .ui-datatable-scrollable-footer'),
    _self = this;
    
    if(this.cfg.scrollWidth) {
        scrollHeader.width(this.cfg.scrollWidth);
        scrollBody.width(this.cfg.scrollWidth);
        scrollFooter.width(this.cfg.scrollWidth);
    }
    
    if(this.cfg.liveScroll) {
        this.scrollOffset = this.cfg.scrollStep;
        this.shouldLiveScroll = true;       
    }
    
    scrollBody.scroll(function() {
        scrollHeader.scrollLeft(scrollBody.scrollLeft());
        scrollFooter.scrollLeft(scrollBody.scrollLeft());
        
        if(_self.shouldLiveScroll) {
            var scrollTop = this.scrollTop,
            scrollHeight = this.scrollHeight,
            viewportHeight = this.clientHeight;

            if(scrollTop >= (scrollHeight - (viewportHeight))) {
                _self.loadLiveRows();
            }
        }
    });
}

/**
 * Loads rows on-the-fly when scrolling live
 */
PrimeFaces.widget.DataTable.prototype.loadLiveRows = function() {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id,
        formId: this.cfg.formId
    },
    _self = this;

    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                $(_self.jqId + ' .ui-datatable-scrollable-body table tr:last').after(content);

                _self.scrollOffset += _self.cfg.scrollStep;

                // Disable scroll if there is no more data left
                if(_self.scrollOffset == _self.cfg.scrollLimit) {
                    _self.shouldLiveScroll = false;
                }

                if(_self.cfg.resizableColumns) {
                    _self.restoreColumnWidths();
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };

    var params = {};
    params[this.id + "_scrolling"] = true;
    params[this.id + "_scrollOffset"] = this.scrollOffset;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 * Ajax pagination
 */
PrimeFaces.widget.DataTable.prototype.paginate = function(newState) {
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    var _self = this;
    
    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                $(_self.tbody).replaceWith(content);

                if(_self.cfg.resizableColumns) {
                    _self.restoreColumnWidths();
                }
                
                // update checkall checkbox if all enabled checkboxes are
				// checked
                if(_self.checkAllToggler) {
                    var checkboxes = $(_self.jqId + ' tbody.ui-datatable-data:first > tr > td.ui-selection-column .ui-chkbox-box'),
                    uncheckedBoxes = $.grep(checkboxes, function(element) {
                        var checkbox = $(element),
                        disabled = checkbox.hasClass('ui-state-disabled'),
                        checked = checkbox.hasClass('ui-state-active');
                        
                        return !(checked || disabled); 
                    });
                     
                    if(uncheckedBoxes.length == 0)
                        _self.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
                    else
                        _self.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };

    var params = {};
    params[this.id + "_paging"] = true;
    params[this.id + "_first"] = newState.first;
    params[this.id + "_rows"] = newState.rows;
    params[this.id + "_updateBody"] = true;

    options.params = params;
    
    if(this.hasBehavior('page')) {
       var pageBehavior = this.cfg.behaviors['page'];
       
       pageBehavior.call(this, newState, options);
    } else {
       PrimeFaces.ajax.AjaxRequest(options); 
    }
}

/**
 * Ajax sort
 */
PrimeFaces.widget.DataTable.prototype.sort = function(columnId, asc) {
    if(this.isSelectionEnabled()) {
        this.clearSelection();
    }
    
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    var _self = this;
    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                $(_self.tbody).replaceWith(content);
                
                // reset paginator
                var paginator = _self.getPaginator();
                if(paginator) {
                   paginator.setPage(0, true);
                }
                
                if(_self.cfg.resizableColumns) {
                    _self.restoreColumnWidths();
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };
    
    var params = {};
    params[this.id + "_sorting"] = true;
    params[this.id + "_sortKey"] = columnId;
    params[this.id + "_sortDir"] = asc;
    params[this.id + "_updateBody"] = true;

    options.params = params;
    
    if(this.hasBehavior('sort')) {
       var sortBehavior = this.cfg.behaviors['sort'];
       
       sortBehavior.call(this, columnId, options);
    } else {
       PrimeFaces.ajax.AjaxRequest(options); 
    }
}

/**
 * Ajax filter
 */
PrimeFaces.widget.DataTable.prototype.filter = function() {
    if(this.isSelectionEnabled()) {
        this.clearSelection();
    }

    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    var _self = this;
    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");
        
        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                $(_self.tbody).replaceWith(content);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
        
        // update paginator
        var paginator = _self.getPaginator();
        if(paginator) {
            paginator.setTotalRecords(this.args.totalRecords);
        }

        if(_self.cfg.resizableColumns) {
            _self.restoreColumnWidths();
        }
        
        return true;
    };
    
    var params = {};
    params[this.id + "_filtering"] = true;
    params[this.id + "_updateBody"] = true;

    options.params = params;

    if(this.hasBehavior('filter')) {
       var filterBehavior = this.cfg.behaviors['filter'];
       
       filterBehavior.call(this, {}, options);
    } else {
       PrimeFaces.ajax.AjaxRequest(options); 
    }
}

PrimeFaces.widget.DataTable.prototype.onRowClick = function(event, rowElement) {    
    // Check if rowclick triggered this event not a clickable element in row
	// content
    if($(event.target).is('.ui-dt-c,td,span')) {
        var row = $(rowElement),
        selected = row.hasClass('ui-state-highlight');

        if(selected)
            this.unselectRow(row, event);
        else
            this.selectRow(row, event);
        
        PrimeFaces.clearSelection();
    }
}

/**
 * @param r
 *            {Row Index || Row Element}
 */
PrimeFaces.widget.DataTable.prototype.findRow = function(r) {
    var row = r;
    
    if(PrimeFaces.isNumber(r)) {
        row = $(this.tbodyId).children('tr:eq(' + r + ')');
    }
    
    return row;
}

PrimeFaces.widget.DataTable.prototype.selectRow = function(r, event) {
    var row = this.findRow(r),
    rowMeta = this.getRowMeta(row),
    _self = this;

    // unselect previous selection if this is single selection or multiple one
	// with no keys
    if(this.isSingleSelection() || (this.isMultipleSelection() && event && !event.metaKey && !event.shiftKey)) {
        this.unselectAllRows();
    }
    
    if(this.isMultipleSelection() && event && event.shiftKey) {
        var rows = $(this.tbody).children();
        this.originRow = this.originRow||rows.eq(0);
        var originIndex = this.originRow.index();

        // unselect previously selected rows with shift
        if(this.cursor) {
            var oldCursorIndex = this.cursor.index(),
            rowsToUnselect = oldCursorIndex > originIndex ? rows.slice(originIndex, oldCursorIndex + 1) : rows.slice(oldCursorIndex, originIndex + 1);
            
            rowsToUnselect.each(function(i, item) {
                var r = $(item),
                rkey = _self.getRowMeta(r).key;

                r.removeClass('ui-state-highlight');
                _self.removeSelection(rkey);
            });
        }
        
        // select rows between cursor and origin
        this.cursor = row;

        var cursorIndex = this.cursor.index(),
        rowsToSelect = cursorIndex > originIndex ? rows.slice(originIndex, cursorIndex + 1) : rows.slice(cursorIndex, originIndex + 1);

        rowsToSelect.each(function(i, item) {
            var r = $(item),
            rkey = _self.getRowMeta(r).key;

            r.removeClass('ui-state-hover').addClass('ui-state-highlight');
            _self.addSelection(rkey);
        });
        
    }
    else {
        this.originRow = row;
        this.cursor = null;
                
        // add to selection
        row.removeClass('ui-state-hover').addClass('ui-state-highlight');
        this.addSelection(rowMeta.key);
    }

    // save state
    this.writeSelections();

    this.fireRowSelectEvent(rowMeta.key);
}

PrimeFaces.widget.DataTable.prototype.unselectRow = function(r, event) {
    var row = this.findRow(r),
    rowMeta = this.getRowMeta(row);

    if(this.isMultipleSelection() && event && !event.metaKey) {
        this.selectRow(row, event);
    }
    else{
        // remove visual style
        row.removeClass('ui-state-highlight');

        // remove from selection
        this.removeSelection(rowMeta.key);

        // save state
        this.writeSelections();

        this.fireRowUnselectEvent(rowMeta.key);
    }
}

/**
 * Sends a rowSelectEvent on server side to invoke a rowSelectListener if
 * defined
 */
PrimeFaces.widget.DataTable.prototype.fireRowSelectEvent = function(rowKey) {
    if(this.cfg.behaviors) {
        var selectBehavior = this.cfg.behaviors['rowSelect'];
        
        if(selectBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_instantSelectedRowKey'] = rowKey;
            
            selectBehavior.call(this, rowKey, ext);
        }
    }
}

/**
 * Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if
 * defined
 */
PrimeFaces.widget.DataTable.prototype.fireRowUnselectEvent = function(rowKey) {
    if(this.cfg.behaviors) {
        var unselectBehavior = this.cfg.behaviors['rowUnselect'];
        
        if(unselectBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_instantUnselectedRowKey'] = rowKey;
            
            unselectBehavior.call(this, rowKey, ext);
        }
    }
}

/**
 * Selects the corresping row of a radio based column selection
 */
PrimeFaces.widget.DataTable.prototype.selectRowWithRadio = function(row) {
    var rowMeta = this.getRowMeta(row);

    // clean previous selection
    this.selection = [];
    row.siblings('.ui-state-highlight').removeClass('ui-state-highlight'); 
    
    // add to selection
    this.addSelection(rowMeta.key);
    row.addClass('ui-state-highlight');

    // save state
    this.writeSelections();
    
    this.fireRowSelectEvent(rowMeta.key);
}

/**
 * Selects the corresping row of a checkbox based column selection
 */
PrimeFaces.widget.DataTable.prototype.selectRowWithCheckbox = function(checkbox, silent) {
    var row = checkbox.parents('tr:first'),
    rowMeta = this.getRowMeta(row);
    
    // update visuals
    checkbox.addClass('ui-state-active').children('span.ui-chkbox-icon:first').addClass('ui-icon ui-icon-check');
    row.addClass('ui-state-highlight');
    
    // add to selection
    this.addSelection(rowMeta.key);
    
    this.writeSelections();
    
    if(!silent)
        this.fireRowSelectEvent(rowMeta.key);
}

/**
 * Unselects the corresping row of a checkbox based column selection
 */
PrimeFaces.widget.DataTable.prototype.unselectRowWithCheckbox = function(checkbox, silent) {
    var row = checkbox.parents('tr:first'),
    rowMeta = this.getRowMeta(row);
    
    checkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon:first').removeClass('ui-icon ui-icon-check');
    row.removeClass('ui-state-highlight');
    
    // remove from selection
    this.removeSelection(rowMeta.key);

    this.writeSelections();
    
    if(!silent)
        this.fireRowUnselectEvent(rowMeta.key);
}

PrimeFaces.widget.DataTable.prototype.unselectAllRows = function() {
    $(this.tbodyId).children('tr.ui-state-highlight').removeClass('ui-state-highlight'); 
    this.selection = [];
    this.writeSelections();
}

/**
 * Toggles all rows with checkbox
 */
PrimeFaces.widget.DataTable.prototype.toggleCheckAll = function() {
    var checkboxes = $(this.jqId + ' tbody.ui-datatable-data:first > tr > td.ui-selection-column').find('.ui-chkbox > .ui-chkbox-box:not(.ui-state-disabled)'),
    checked = this.checkAllToggler.hasClass('ui-state-active'),
    _self = this;

    if(checked) {
        this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
        
        checkboxes.each(function() {
            _self.unselectRowWithCheckbox($(this), true);
        });
    } 
    else {
        this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
        
        checkboxes.each(function() {
            _self.selectRowWithCheckbox($(this), true);
            
        });
    }

    // save state
    this.writeSelections();
}

/**
 * Expands a row to display detail content
 */
PrimeFaces.widget.DataTable.prototype.toggleExpansion = function(expanderElement) {
    var expander = $(expanderElement),
    row = expander.parents('tr:first'),
    rowIndex = this.getRowMeta(row).index,
    expanded = row.hasClass('ui-expanded-row'),
    _self = this;
    
    // Run toggle expansion if row is not being toggled already to prevent
	// conflicts
    if($.inArray(rowIndex, this.expansionProcess) == -1) {
        if(expanded) {
            this.expansionProcess.push(rowIndex);
            expander.removeClass('ui-icon-circle-triangle-s');
            row.removeClass('ui-expanded-row');

            row.next().fadeOut(function() {
               $(this).remove();

               _self.expansionProcess = $.grep(_self.expansionProcess, function(r) {
                   return r != rowIndex;
               });
            });
        }
        else {
            this.expansionProcess.push(rowIndex);
            expander.addClass('ui-icon-circle-triangle-s');
            row.addClass('ui-expanded-row');

            this.loadExpandedRowContent(row);
        }
    }
}

PrimeFaces.widget.DataTable.prototype.loadExpandedRowContent = function(row) {
    if(this.cfg.onExpandStart) {
        this.cfg.onExpandStart.call(this, row);
    }

    var options = {
        source: this.id,
        process: this.id,
        update: this.id,
        formId: this.cfg.formId
    },
    rowIndex = this.getRowMeta(row).index,
    _self = this;

    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                row.after(content);
                row.next().fadeIn();
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };
    
    options.oncomplete = function() {
        _self.expansionProcess = $.grep(_self.expansionProcess, function(r) {
           return r != rowIndex;
       });
    };

    var params = {};
    params[this.id + '_rowExpansion'] = true;
    params[this.id + '_expandedRowIndex'] = rowIndex;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 * Displays in-cell editors for given row
 */
PrimeFaces.widget.DataTable.prototype.showEditors = function(el) {
    var element = $(el);
    
    element.parents('tr:first').addClass('ui-state-highlight').children('td.ui-editable-column').each(function() {
        var column = $(this);

        column.find('span.ui-cell-editor-output').hide();
        column.find('span.ui-cell-editor-input').show();

        if(element.hasClass('ui-icon-pencil')) {
            element.hide().siblings().show();
        }
    });
}

/**
 * Saves the edited row
 */
PrimeFaces.widget.DataTable.prototype.saveRowEdit = function(element) {
    this.doRowEditRequest(element, 'save');
}

/**
 * Cancels row editing
 */
PrimeFaces.widget.DataTable.prototype.cancelRowEdit = function(element) {
    this.doRowEditRequest(element, 'cancel');
}

/**
 * Sends an ajax request to handle row save or cancel
 */
PrimeFaces.widget.DataTable.prototype.doRowEditRequest = function(element, action) {
    var row = $(element).parents('tr').eq(0),
    options = {
        source: this.id,
        update: this.id,
        formId: this.cfg.formId
    },
    _self = this,
    rowEditorId = row.find('span.ui-row-editor').attr('id'),
    expanded = row.hasClass('ui-expanded-row');

    if(action === 'save') {
        // Only process cell editors of current row
        var editorsToProcess = new Array();

        row.find('span.ui-cell-editor').each(function() {
           editorsToProcess.push($(this).attr('id'));
        });

        options.process = editorsToProcess.join(' ');
    }

    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
        
        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                if(!this.args.validationFailed) {
                    // remove row expansion
                    if(expanded) {
                       row.next().remove();
                    }

                    row.replaceWith(content);
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }

        return true;
    };

    var params = {};
    params[rowEditorId] = rowEditorId;
    params[this.id + '_rowEdit'] = true;
    params[this.id + '_editedRowIndex'] = this.getRowMeta(row).index;
    
    if(action === 'cancel') {
        params[this.id + '_rowEditCancel'] = true;
    }

    options.params = params;
    
    if(this.hasBehavior('rowEdit')) {
       var rowEditBehavior = this.cfg.behaviors['rowEdit'];
       
       rowEditBehavior.call(this, row, options);
    } else {
       PrimeFaces.ajax.AjaxRequest(options); 
    }
}


/**
 * Returns the paginator instance if any defined
 */
PrimeFaces.widget.DataTable.prototype.getPaginator = function() {
    return this.paginator;
}

/**
 * Writes selected row ids to state holder
 */
PrimeFaces.widget.DataTable.prototype.writeSelections = function() {
    $(this.selectionHolder).val(this.selection.join(','));
}

/**
 * Returns type of selection
 */
PrimeFaces.widget.DataTable.prototype.isSingleSelection = function() {
    return this.cfg.selectionMode == 'single';
}

PrimeFaces.widget.DataTable.prototype.isMultipleSelection = function() {
    return this.cfg.selectionMode == 'multiple';
}

/**
 * Clears the selection state
 */
PrimeFaces.widget.DataTable.prototype.clearSelection = function() {
    this.selection = [];
    
    $(this.selectionHolder).val('');
}

/**
 * Returns true|false if selection is enabled|disabled
 */
PrimeFaces.widget.DataTable.prototype.isSelectionEnabled = function() {
    return this.cfg.selectionMode != undefined || this.cfg.columnSelectionMode != undefined;
}

/**
 * Returns true|false if datatable has incell editors
 */
PrimeFaces.widget.DataTable.prototype.getRowEditors = function() {
    return $(this.jqId + ' tbody.ui-datatable-data tr td span.ui-row-editor');
}

/**
 * Binds cell editor events non-obstrusively
 */
PrimeFaces.widget.DataTable.prototype.setupCellEditorEvents = function(rowEditors) {
    var _self = this;
    
    rowEditors.find('span.ui-icon-pencil').die().live('click', function() {
        _self.showEditors(this);
    });

    rowEditors.find('span.ui-icon-check').die().live('click', function() {
        _self.saveRowEdit(this);
    });

    rowEditors.find('span.ui-icon-close').die().live('click', function() {
        _self.cancelRowEdit(this);
    });
}

/**
 * Clears table filters and then update the datatable
 */
PrimeFaces.widget.DataTable.prototype.clearFilters = function() {
    $(this.jqId + ' thead th .ui-column-filter').val('');
}

/**
 * Add resize behavior to columns
 */
 PrimeFaces.widget.DataTable.prototype.setupResizableColumns = function() {
    // Add resizers and resizer helper
    $(this.jqId + ' thead tr th').prepend('<span class="ui-column-resizer">&nbsp;</span>');
    $(this.jqId).append('<div class="ui-column-resizer-helper ui-state-highlight"></div>');

    // Variables
    var resizerHelper = $(this.jqId + ' .ui-column-resizer-helper'),
    resizers = $(this.jqId + ' thead th span.ui-column-resizer'),
    scrollHeader = $(this.jqId + ' .ui-datatable-scrollable-header'),
    scrollBody = $(this.jqId + ' .ui-datatable-scrollable-body'),
    table = $(this.jqId + ' table'),
    thead = $(this.jqId + ' thead'),  
    tfoot = $(this.jqId + ' tfoot'),
    _self = this;
         
    // State cookie
    this.columnWidthsCookie = location.href + '_' + this.id + '_columnWidths';
    
    // Main resize events
    resizers.draggable({
        axis: 'x',
        start: function(event, ui) {
            var height = _self.cfg.scrollable ? scrollBody.height() : table.height() - thead.height() - 1;

            // Set height of resizer helper
            resizerHelper.height(height);
            resizerHelper.show();
        },
        drag: function(event, ui) {
            resizerHelper.offset(
                {
                    left: ui.helper.offset().left + ui.helper.width() / 2, 
                    top: thead.offset().top + thead.height()
                });  
        },
        stop: function(event, ui) {
            var columnHeader = ui.helper.parent(),
            columnHeaderWrapper = columnHeader.children('div.ui-dt-c'),
            oldPos = ui.originalPosition.left,
            newPos = ui.position.left,
            change = (newPos - oldPos),
            newWidth = (columnHeaderWrapper.width() + change - (ui.helper.width() / 2)),
            tbody = $(_self.jqId + ' tbody');
            
            ui.helper.css('left','');
            resizerHelper.hide();
            
            columnHeaderWrapper.width(newWidth);
            columnHeader.css('width', '');
                        
            tbody.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);            
            tfoot.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);

            scrollHeader.scrollLeft(scrollBody.scrollLeft());

            // Save state
            _self.saveColumnWidths();
            
            // Invoke colResize behavior
            if(_self.hasBehavior('colResize')) {
                var colResizeBehavior = _self.cfg.behaviors['colResize'];
                
                var ext = {
                    params: {}
                };
                ext.params[_self.id + '_columnId'] = columnHeader.attr('id');
                ext.params[_self.id + '_width'] = newWidth;
                ext.params[_self.id + '_height'] = columnHeader.height();
                
                colResizeBehavior.call(_self, event, ext);
                
            }
        },
        containment: this.jq
    });
    
    this.restoreColumnWidths();
}

PrimeFaces.widget.DataTable.prototype.saveColumnWidths = function() {
    var columnWidths = [],
    columnHeaders = this.cfg.scrollable ? this.jq.find('.ui-datatable-scrollable-header thead th') : this.jq.find('table thead th');
    
    columnHeaders.each(function(i, item) {
        columnWidths.push($(item).children('.ui-dt-c').width());
    });
    PrimeFaces.setCookie(this.columnWidthsCookie, columnWidths.join(','));
}

PrimeFaces.widget.DataTable.prototype.restoreColumnWidths = function() {
    var widths = PrimeFaces.getCookie(this.columnWidthsCookie),
    columnHeaders = this.cfg.scrollable ? this.jq.find('.ui-datatable-scrollable-header thead th') : this.jq.find('table thead th'),
    columnFooters = this.cfg.scrollable ? this.jq.find('.ui-datatable-scrollable-footer tfoot td') : this.jq.find('table tfoot td');
    
    if(widths) {
        widths = widths.split(',');
        for(var i = 0; i < widths.length; i++) {
            var width = widths[i];
            
            columnHeaders.eq(i).children('.ui-dt-c').width(width);
            $(this.jqId + ' tbody.ui-datatable-data').find('tr td:nth-child(' + (i + 1) + ')').children('.ui-dt-c').width(width);
            columnFooters.eq(i).children('.ui-dt-c').width(width);
        }
    }
}

PrimeFaces.widget.DataTable.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

/**
 * Remove given rowIndex from selection
 */
PrimeFaces.widget.DataTable.prototype.removeSelection = function(rowIndex) {
    var selection = this.selection;
    
    $.each(selection, function(index, value) {
        if(value === rowIndex) {
            selection.remove(index);
            
            return false;       // break
        } 
        else {
            return true;        // continue
        }
    });
}

/**
 * Adds given rowIndex to selection if it doesn't exist already
 */
PrimeFaces.widget.DataTable.prototype.addSelection = function(rowIndex) {
    if(!this.isSelected(rowIndex)) {
        this.selection.push(rowIndex);
    }
}

/**
 * Finds if given rowIndex is in selection
 */
PrimeFaces.widget.DataTable.prototype.isSelected = function(rowIndex) {
    var selection = this.selection,
    selected = false;
    
    $.each(selection, function(index, value) {
        if(value === rowIndex) {
            selected = true;
            
            return false;       // break
        } 
        else {
            return true;        // continue
        }
    });
    
    return selected;
}

PrimeFaces.widget.DataTable.prototype.getRowMeta = function(row) {
    var meta = {
        index: row.data('ri'),
        key:  row.data('rk')
    };
    
    return meta;
}

/**
 * Moves widths of columns to column wrappers
 */
PrimeFaces.widget.DataTable.prototype.alignColumnWidths = function() {
    this.jq.find('div.ui-dt-c').each(function() {
        var wrapper = $(this),
        column = wrapper.parent();
        
        wrapper.width(column.width());
        column.width('');
    });
} 

PrimeFaces.widget.Dialog = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.content = this.jq.children('.ui-dialog-content');
    this.titlebar = this.jq.children('.ui-dialog-titlebar');
    this.footer = this.jq.find('.ui-dialog-footer');
    this.icons = this.titlebar.children('.ui-dialog-titlebar-icon');
    this.closeIcon = this.titlebar.children('.ui-dialog-titlebar-close');
    this.minimizeIcon = this.titlebar.children('.ui-dialog-titlebar-minimize');
    this.maximizeIcon = this.titlebar.children('.ui-dialog-titlebar-maximize');
    this.visible = false;
    this.blockEvents = 'focus.dialog mousedown.dialog mouseup.dialog keydown.dialog keypress.dialog click.dialog';
    this.onshowHandlers = [];
    
    // configuration
    this.cfg.width = this.cfg.width||'auto';
    if(this.cfg.width == 'auto' && $.browser.msie && parseInt($.browser.version, 10) == 7) {
        this.cfg.width = 300;
    }
    this.cfg.height = this.cfg.height||'auto';
    this.cfg.draggable = this.cfg.draggable == false ? false : true;
    this.cfg.resizable = this.cfg.resizable == false ? false : true;
    this.cfg.minWidth = this.cfg.minWidth||150;
    this.cfg.minHeight = this.cfg.minHeight||this.titlebar.outerHeight();
    this.cfg.position = this.cfg.position||'center';
    this.parent = this.jq.parent();
    
    // size
    this.jq.css({
        'width': this.cfg.width,
        'height': 'auto'
    });
    
    this.content.height(this.cfg.height);
    
    // position
    this.initPosition();
 
    // events
    this.bindEvents();
    
    if(this.cfg.draggable) {
        this.setupDraggable();
    }
    
    if(this.cfg.resizable){
        this.setupResizable();
    }
    
    if(this.cfg.modal) {
        this.syncWindowResize();
    }
    
    if(this.cfg.appendToBody){
        this.jq.appendTo('body');
    }
        
    // docking zone
    if($(document.body).children('.ui-dialog-docking-zone').length == 0) {
        $(document.body).append('<div class="ui-dialog-docking-zone"></div>')
    }
        
    if(this.cfg.autoOpen){
        this.show();
    }
    
    this.jq.data('widget', this);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Dialog, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Dialog.prototype.enableModality = function() {
    var _self = this;

    $(document.body).append('<div id="' + this.id + '_modal" class="ui-widget-overlay"></div>').
        children(this.jqId + '_modal').css({
            'width': $(document).width()
            ,'height': $(document).height()
            ,'z-index': this.jq.css('z-index') - 1
        });

    // disable tabbing out of modal dialog and stop events from targets outside
	// of dialog
    $(document).bind('keypress.dialog', function(event) {
        if(event.keyCode == $.ui.keyCode.TAB) {
            var tabbables = _self.content.find(':tabbable'),
            first = tabbables.filter(':first'),
            last  = tabbables.filter(':last');

            if(event.target === last[0] && !event.shiftKey) {
                first.focus(1);
                return false;
            } 
            else if (event.target === first[0] && event.shiftKey) {
                last.focus(1);
                return false;
            }
        }        
    })
    .bind(this.blockEvents, function(event) {
        if ($(event.target).zIndex() < PrimeFaces.zindex) {
            return false;
        }
    });
}

PrimeFaces.widget.Dialog.prototype.disableModality = function(){
    $(document.body).children(this.jqId + '_modal').remove();
    $(document).unbind(this.blockEvents).unbind('keydown.dialog');
}

PrimeFaces.widget.Dialog.prototype.syncWindowResize = function() {
    $(window).resize(function() {
        $(document.body).children('.ui-widget-overlay').css({
            'width': $(document).width()
            ,'height': $(document).height()
        });
    });
}

PrimeFaces.widget.Dialog.prototype.show = function() {
    if(this.visible) {
       return;
    }

    if(!this.loaded && this.cfg.dynamic) {
        this.loadContents();
    } 
    else {
        this._show();
    }
}

PrimeFaces.widget.Dialog.prototype._show = function() {
    if(this.cfg.showEffect) {
        var _self = this;
            
        this.jq.show(this.cfg.showEffect, null, 'normal', function() {
            _self.postShow();
        });
    }    
    else {
        // display dialog
        this.jq.show();
        
        this.postShow();
    }
    
    this.focusFirstInput();
    this.visible = true;
    this.moveToTop();
    
    if(this.cfg.modal)
        this.enableModality();
}

PrimeFaces.widget.Dialog.prototype.postShow = function() {   
    // execute user defined callback
    if(this.cfg.onShow) {
        this.cfg.onShow.call(this);
    }
            
    // execute onshowHandlers and remove successful ones
    this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
		return !fn.call();
	});
}

PrimeFaces.widget.Dialog.prototype.hide = function() {   
    if(!this.visible) {
       return;
    }

    if(this.cfg.hideEffect) {
        var _self = this;
    
        this.jq.hide(this.cfg.hideEffect, null, 'normal', function() {
            _self.onHide();
        });
    }
    else {
        this.jq.hide();
        
        this.onHide();
    }
    
    this.visible = false;
    
    if(this.cfg.modal)
        this.disableModality();
}

PrimeFaces.widget.Dialog.prototype.focusFirstInput = function() {
    this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
}

PrimeFaces.widget.Dialog.prototype.bindEvents = function() {   
    var _self = this;
    
    this.jq.mousedown(function(){
        _self.moveToTop();
    });

    this.icons.mouseover(function() {
        $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    })
        
    this.closeIcon.click(function(e) {
        _self.hide();
        e.preventDefault();
    });
    
    this.maximizeIcon.click(function(e) {
        _self.toggleMaximize();
        e.preventDefault();
    });
    
    this.minimizeIcon.click(function(e) {
        _self.toggleMinimize();
        e.preventDefault();
    });
}

PrimeFaces.widget.Dialog.prototype.setupDraggable = function() {    
    this.jq.draggable({
        cancel: '.ui-dialog-content, .ui-dialog-titlebar-close',
        handle: '.ui-dialog-titlebar',
        containment : 'document'
    });
}
PrimeFaces.widget.Dialog.prototype.setupResizable = function() {    
    this.jq.resizable({
        handles : 'n,s,e,w,ne,nw,se,sw',
        minWidth : this.cfg.minWidth,
        minHeight : this.cfg.minHeight,
        alsoResize : this.content,
        containment: 'document'
    });
    
    this.resizers = this.jq.children('.ui-resizable-handle');
}

PrimeFaces.widget.Dialog.prototype.initPosition = function() {
    
    if(/(center|left|top|right|bottom)/.test(this.cfg.position)) {
        this.cfg.position = this.cfg.position.replace(',', ' ');
        
        this.jq.position({
            my: 'center'
            ,at: this.cfg.position
            ,collision: 'fit'
            ,of: window
            ,using: function(pos) {
                var topOffset = $(this).css(pos).offset().top;
                if(topOffset < 0) {
                    $(this).css('top', pos.top - topOffset);
                }
            }
        });
    }
    else {
        var coords = this.cfg.position.split(','),
        x = $.trim(coords[0]),
        y = $.trim(coords[1]);
        
        this.jq.offset({
            top: y
            ,left: x
        });
    }
    
}

PrimeFaces.widget.Dialog.prototype.onHide = function(event, ui) {
    if(this.cfg.onHide) {
        this.cfg.onHide.call(this, event, ui);
    }

    if(this.cfg.behaviors) {
        var closeBehavior = this.cfg.behaviors['close'];

        if(closeBehavior) {
            closeBehavior.call(this);
        }
    }
}

PrimeFaces.widget.Dialog.prototype.moveToTop = function() {
    this.jq.css('z-index', ++PrimeFaces.zindex);
}

PrimeFaces.widget.Dialog.prototype.toggleMaximize = function() {
    if(this.minimized) {
        this.toggleMinimize();
    }
    
    if(this.maximized) {
        this.restoreState(true);
                
        this.maximizeIcon.children('.ui-icon').removeClass('ui-icon-newwin').addClass('ui-icon-extlink');
        this.maximized = false;
    } 
    else {
        this.saveState(true);
        
        var win = $(window);
                
        this.jq.css({
            'width': win.width() - 6
            ,'height': win.height()
           }).offset({
               top: win.scrollTop()
               ,left: win.scrollLeft()
           });
        
        // maximize content
        this.content.css({
            width: 'auto',
            height: 'auto'
        });
        
        this.maximizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-extlink').addClass('ui-icon-newwin');
        this.maximized = true;
    }
}

PrimeFaces.widget.Dialog.prototype.toggleMinimize = function() {
    var animate = true,
    dockingZone = $(document.body).children('.ui-dialog-docking-zone');
    
    if(this.maximized) {
        this.toggleMaximize();
        animate = false;
    }
    
    var _self = this;
    
    if(this.minimized) {
        this.jq.appendTo(this.parent).css({'position':'fixed', 'float':'none'});
        this.restoreState(false);
        this.content.show();
        this.minimizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-plus').addClass('ui-icon-minus');
        this.minimized = false;
        
        if(this.cfg.resizable)
            this.resizers.show();
    }
    else {
        this.saveState(false);
        
        if(animate) {
            this.jq.effect('transfer', {
                            to: dockingZone
                            ,className: 'ui-dialog-minimizing'
                            }, 500, 
                            function() {
                                _self.dock(dockingZone);
                            });
        } 
        else {
            this.dock(dockingZone);
        }
    }
}

PrimeFaces.widget.Dialog.prototype.dock = function(zone) {
    this.jq.appendTo(zone).css('position', 'static');
    this.jq.css({'height':'auto', 'width':'auto', 'float': 'left'});
    this.content.hide();
    this.minimizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-minus').addClass('ui-icon-plus');
    this.minimized = true;
    
    if(this.cfg.resizable)
        this.resizers.hide();
}

PrimeFaces.widget.Dialog.prototype.saveState = function(includeOffset) {
    this.state = {
        width: this.jq.width()
        ,height: this.jq.height()
    };
    
    var win = $(window);
    if(includeOffset) {
        this.state.offset = this.jq.offset();
        this.state.windowScrollLeft = win.scrollLeft();
        this.state.windowScrollTop = win.scrollTop();
    }
}

PrimeFaces.widget.Dialog.prototype.restoreState = function(includeOffset) {
    this.jq.width(this.state.width).height(this.state.height);
        
    if(includeOffset) {
        var win = $(window);
        this.jq.offset({
           top: this.state.offset.top + (win.scrollTop() - this.state.windowScrollTop)
           ,left: this.state.offset.left + (win.scrollLeft() - this.state.windowScrollLeft)
        });
    }
}

PrimeFaces.widget.Dialog.prototype.loadContents = function() {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id
    },
    _self = this;

    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                _self.content.html(content);
                _self.loaded = true;
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }

        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
        
        return true;
    };
    
    options.oncomplete = function() {
        _self._show();
    };

    var params = [];
    params[this.id + '_contentLoad'] = true;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.Dialog.prototype.addOnshowHandler = function(fn) {
    this.onshowHandlers.push(fn);
}

PrimeFaces.widget.Dialog.prototype.getScriptTag = function() {
    return this.getJQ().prev('script');
}

/**
 * PrimeFaces ConfirmDialog Widget
 */
PrimeFaces.widget.ConfirmDialog = function(cfg) {
    cfg.draggable = false;
    cfg.resizable = false;
    cfg.modal = true;
    cfg.showEffect = 'fade';
    cfg.hideEffect = 'fade';
    
    PrimeFaces.widget.Dialog.call(this, cfg);
}

PrimeFaces.widget.ConfirmDialog.prototype = PrimeFaces.widget.Dialog.prototype;
/**
 * PrimeFaces Draggable Widget
 */
PrimeFaces.widget.Draggable = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(PrimeFaces.escapeClientId(this.cfg.target));
	
    this.jq.draggable(this.cfg);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Draggable, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Draggable.prototype.getScriptTag = function() {
    return $(this.jqId + '_script');
}

/**
 * PrimeFaces Droppable Widget
 */
PrimeFaces.widget.Droppable = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(PrimeFaces.escapeClientId(this.cfg.target));
	
    this.bindDropListener();
    	
    this.jq.droppable(this.cfg);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Droppable, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Droppable.prototype.getScriptTag = function() {
    return $(this.jqId + '_script');
}

PrimeFaces.widget.Droppable.prototype.bindDropListener = function() {
    var _self = this;
    
    this.cfg.drop = function(event, ui) {
        if(_self.cfg.onDrop) {
            _self.cfg.onDrop.call(_self, event, ui);
        }
        if(_self.cfg.behaviors) {
            var dropBehavior = _self.cfg.behaviors['drop'];

            if(dropBehavior) {
                var ext = {
                    params: {}
                };
                ext.params[_self.id + "_dragId"] = ui.draggable.attr('id');
                ext.params[_self.id + "_dropId"] = _self.cfg.target;
                
                dropBehavior.call(_self, event, ext);
            }
        }
    };
}

/**
	 * PrimeFaces Effect Widget
	 */
PrimeFaces.widget.Effect = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.source = $(PrimeFaces.escapeClientId(this.cfg.source));
    var _self = this;
    
    this.runner = function() {
        // avoid queuing multiple runs
        if(_self.timeoutId) {
            clearTimeout(_self.timeoutId);
        }
        
        _self.timeoutId = setTimeout(_self.cfg.fn, _self.cfg.delay);
    };
    
    if(this.cfg.event == 'load') {
        this.runner.call();
    } 
    else {
        this.source.bind(this.cfg.event, this.runner);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Effect, PrimeFaces.widget.BaseWidget);

/**
																			 * PrimeFaces
																			 * Fieldset
																			 * Widget
																			 */
PrimeFaces.widget.Fieldset = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.legend = this.jq.children('.ui-fieldset-legend');

    var _self = this;
    
    if(this.cfg.toggleable) {
        
        this.content = this.jq.children('.ui-fieldset-content');
        this.toggler = this.legend.children('.ui-fieldset-toggler');
        this.stateHolder = $(this.jqId + '_collapsed');

        // Add clickable legend state behavior
        this.legend.click(function(e) {_self.toggle(e);})
                           .mouseover(function() {_self.legend.toggleClass('ui-state-hover');})
                           .mouseout(function() {_self.legend.toggleClass('ui-state-hover');})
                           .mousedown(function() {_self.legend.toggleClass('ui-state-active');})
                           .mouseup(function() {_self.legend.toggleClass('ui-state-active');})
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Fieldset, PrimeFaces.widget.BaseWidget);

/**
 * Toggles the content
 */
PrimeFaces.widget.Fieldset.prototype.toggle = function(e) {
    this.updateToggleState(this.cfg.collapsed);
    
    var _self = this;

    this.content.slideToggle(this.cfg.toggleSpeed, function() {
        if(_self.cfg.behaviors) {
            var toggleBehavior = _self.cfg.behaviors['toggle'];

            if(toggleBehavior) {
                toggleBehavior.call(_self);
            }
        }
    });
}

/**
 * Updates the visual toggler state and saves state
 */
PrimeFaces.widget.Fieldset.prototype.updateToggleState = function(collapsed) {
    if(collapsed)
        this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
    else
        this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');

    this.cfg.collapsed = !collapsed;
    
    this.stateHolder.val(!collapsed);
};

(function($) {
    /**
	 * The autoResizable() function is used to animate auto-resizable textareas
	 * on a given selector. To change the default options, simply pass the
	 * options in an object as the only argument to the autoResizable()
	 * function.
	 */
    $.fn.autoResizable = function(options) {

        // Defines the abstract settings.
        var settings = $.extend({
            animate: true,
            animateDuration: 200,
            maxHeight: 500,
            onBeforeResize: null,
            onAfterResize: null,
            padding: 20,
            paste: true,
            pasteInterval: 100
        }, options);

        // Filters the selectors to just textareas.
        return this.filter('textarea').each(function() {
            var textarea = $(this),
            originalHeight = textarea.height(),
            currentHeight = 0,
            pasteListener = null,
            animate = settings.animate,
            animateDuration = settings.animateDuration,
            maxHeight = settings.maxHeight,
            onBeforeResize = settings.onBeforeResize,
            onAfterResize = settings.onAfterResize,
            padding = settings.padding,
            paste = settings.paste,
            pasteInterval = settings.pasteInterval;

            // Creates a clone of the textarea, used to determine the textarea
			// height.
            var clone = (function() {
                var cssKeys = ['height', 'letterSpacing', 'lineHeight', 'textDecoration', 'width'],
                properties = {};

                $.each(cssKeys, function(i, key) {
                    properties[key] = textarea.css(key);
                });

                return textarea.clone().removeAttr('id').removeAttr('name').css({
                    left: -99999,
                    position: 'absolute',
                    top: -99999
                }).css(properties).attr('tabIndex', -1).insertBefore(textarea);
            })();

            /**
			 * Automatically resizes the textarea.
			 */
            var autoResize = function() {
                if (originalHeight <= 0) {
                    originalHeight = textarea.height();
                }

                // Prepares the clone.
                clone.height(0).val(textarea.val()).scrollTop(10000);

                // Determines the height of the text.
                var newHeight = Math.max((clone.scrollTop() + padding), originalHeight);
                if (newHeight === currentHeight || (newHeight >= maxHeight && currentHeight === maxHeight)) {
                    return;
                }

                if (newHeight >= maxHeight) {
                    newHeight = maxHeight;
                    textarea.css('overflow-y', 'auto');
                } else {
                    textarea.css({
                        overflow: 'hidden', 
                        overflowY: 'hidden'
                    });
                }

                // Fires off the onBeforeResize event.
                var resize = true;
                if (onBeforeResize !== null) {
                    resize = onBeforeResize.call(textarea, currentHeight, newHeight);
                }

                currentHeight = newHeight;

                // Determines if the resizing should actually take place.
                if (resize === false) {
                    return;
                }

                // Adjusts the height of the textarea.
                if (animate && textarea.css('display') === 'block') {
                    textarea.stop().animate({
                        height: newHeight
                    }, animateDuration, function() {
                        if (onAfterResize !== null) {
                            onAfterResize.call(textarea);
                        }
                    });
                } else {
                    textarea.height(newHeight);
                    if (onAfterResize !== null) {
                        onAfterResize.call(textarea);
                    }
                }
            };

            /**
			 * Initialises the paste listener and invokes the autoResize method.
			 */
            var init = function() {
                if (paste) {
                    pasteListener = setInterval(autoResize, pasteInterval);
                }

                autoResize();
            };

            /**
			 * Uninitialises the paste listener.
			 */
            var uninit = function() {
                if (pasteListener !== null) {
                    clearInterval(pasteListener);
                    pasteListener = null;
                }
            };

            // Hides scroll bars and disables WebKit resizing.
            textarea.css({
                overflow: 'hidden', 
                resize: 'none'
            });

            // Binds the textarea event handlers.
            textarea.unbind('.autoResizable')
            .bind('keydown.autoResizable', autoResize)
            .bind('keyup.autoResizable', autoResize)
            .bind('change.autoResizable', autoResize)
            .bind('focus.autoResizable', init)
            .bind('blur.autoResizable', uninit);
        });
    };
})(jQuery);


/**
 * PrimeFaces InputText Widget
 */
PrimeFaces.widget.InputText = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    // Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    // Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.jq);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.InputText, PrimeFaces.widget.BaseWidget);

/**
 * PrimeFaces InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');
    var _self = this;
    
    // Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.input);
    }

    // AutoResize
    if(this.cfg.autoResize) {
        this.input.autoResizable({
            maxHeight: this.cfg.maxHeight,
            animateDuration: this.cfg.effectDuration
        });
    }
    
    // max length
    if(this.cfg.maxlength){
        // backspace, tab, pageup/down, end, arrows..
        var ignore = [8,9,33,34,35,36,37,38,39,40,46];
        this.input.keydown(function(e){
            return $(this).val().length < _self.cfg.maxlength 
            || $.inArray(e.which, ignore) !== -1
            || e.metaKey;
        });
    }

    // Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.InputTextarea, PrimeFaces.widget.BaseWidget);

/**
 * PrimeFaces SelectOneMenu Widget
 */
PrimeFaces.widget.SelectOneMenu = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.panelId = this.jqId + '_panel';
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');
    this.labelContainer = this.jq.find('.ui-selectonemenu-label-container');
    this.label = this.jq.find('.ui-selectonemenu-label');
    this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');
    this.triggers = this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');
    this.panel = this.jq.children(this.panelId);
    this.disabled = this.jq.hasClass('ui-state-disabled');
    this.tabindex = this.labelContainer.attr("tabindex") || 0;
    this.itemContainer = this.panel.children('.ui-selectonemenu-items');
    this.options = this.input.children('option:not(:disabled)');
    this.cfg.effectDuration = this.cfg.effectDuration||400;
    
    var _self = this;

    // disable options
    this.options.filter(':disabled').each(function() {
        _self.itemContainer.children().eq($(this).index()).addClass('ui-state-disabled');
    });
    
    // enabled items
    this.items = this.itemContainer.find('.ui-selectonemenu-item:not(.ui-state-disabled)');

    // populate label and activate selected item
    var selectedOption = this.options.filter(':selected');
    this.label.html(selectedOption.text());
    this.items.eq(selectedOption.index()).addClass('ui-state-active');
    
    this.bindEvents();

    // disable tabbing if disabled
    if(this.disabled) {
        this.labelContainer.attr("tabindex", -1);
    }

    // Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    // Append panel to body
    $(document.body).children(this.panelId).remove();
    this.panel.appendTo(document.body);
    
    // align panel and label-menuicon
    var panelWidth = this.panel.width(),
    jqWidth = this.jq.width();
    
    if(panelWidth > jqWidth) {
        this.jq.width(panelWidth + this.menuIcon.width());
        this.panel.width(this.jq.width());
    }
    else {
        this.panel.width(jqWidth);
        this.jq.width(jqWidth);     // replace auto with fixed width
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectOneMenu, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectOneMenu.prototype.bindEvents = function() {
    var _self = this;

    // Events for items
    this.items.mouseover(function() {
        _self.highlightItem($(this));
    }).click(function() {
        _self.selectItem($(this));
    });

    // Events to show/hide the panel
    this.triggers.mouseover(function() {
        if(!_self.disabled) {
            _self.triggers.addClass('ui-state-hover');
        }
    }).mouseout(function() {
        if(!_self.disabled) {
            _self.triggers.removeClass('ui-state-hover');
        }
    }).click(function(e) {
        if(!_self.disabled) {
            if(_self.panel.is(":hidden"))
                _self.show();
            else
                _self.hide();
        }
        
        _self.triggers.removeClass('ui-state-hover').addClass('ui-state-focus');
        e.preventDefault();
    });


    var offset;
    // hide overlay when outside is clicked
    $(document.body).bind('click', function (e) {
        if (_self.panel.is(":hidden")) {
            return;
        }
        offset = _self.panel.offset();
        if (e.target === _self.label.get(0) ||
            e.target === _self.menuIcon.get(0) ||
            e.target === _self.menuIcon.children().get(0)) {
            return;
        }
        if (e.pageX < offset.left ||
            e.pageX > offset.left + _self.panel.width() ||
            e.pageY < offset.top ||
            e.pageY > offset.top + _self.panel.height()) {
            _self.hide();
        }
        _self.hide();
    });
    
    this.labelContainer.focus(function(){
        if(!_self.disabled){
            _self.triggers.addClass('ui-state-focus');
        }
    }).blur(function(){
        if(!_self.disabled){
            _self.triggers.removeClass('ui-state-focus');
        }
    });
    
    // key bindings
    this.bindKeyEvents();
}

PrimeFaces.widget.SelectOneMenu.prototype.highlightItem = function(item) {
    this.unhighlightItem(this.items.filter('.ui-state-active'));
    item.addClass('ui-state-active');
}

PrimeFaces.widget.SelectOneMenu.prototype.unhighlightItem = function(item) {
    item.removeClass('ui-state-active');
}

PrimeFaces.widget.SelectOneMenu.prototype.selectItem = function(item) {
    var option = this.options.eq(item.index()),
    optionLabel = option.text();

    // select item
    this.unhighlightItem(this.items.filter('.ui-state-active'));
    item.addClass('ui-state-active');
    option.attr('selected', 'selected');

    if($.trim(optionLabel) != '')
        this.label.text(optionLabel);
    else
        this.label.html('&nbsp;');

    this.labelContainer.focus();
    this.input.change();
    this.hide();
}

PrimeFaces.widget.SelectOneMenu.prototype.bindKeyEvents = function() {
    this.highlightItems = [];
    this.highlightKeyPath = '';
    this.highlightOption = null;
    this.highlightTimer = null;
    
    var _self = this;

    this.labelContainer.keydown(function(e) {
        if(_self.disabled)
            return;
        
        if(_self.highlightTimer != null)
            clearTimeout(_self.highlightTimer);

        _self.highlightTimer = setTimeout(function(){
            _self.highlightKeyPath = '';
        }, 1000);

        var keyCode = $.ui.keyCode;

        switch (e.which) {
            case keyCode.UP:
            case keyCode.LEFT:
                var highlightedItem = _self.items.filter('.ui-state-active'),
                prev = highlightedItem.prevAll(':not(.ui-state-disabled):first');
                
                if(prev.length == 1) {
                    if(_self.panel.is(':visible'))
                       _self.highlightItem(prev);
                   else
                        _self.selectItem(prev);
                }

                e.preventDefault();
                break;

            case keyCode.DOWN:
            case keyCode.RIGHT:
                var highlightedItem = _self.items.filter('.ui-state-active'), 
                next = highlightedItem.nextAll(':not(.ui-state-disabled):first');
                
                if(next.length == 1) {
                   if(_self.panel.is(':visible'))
                       _self.highlightItem(next);
                   else
                        _self.selectItem(next);
                }
                
                e.preventDefault();
                break;
                
            case keyCode.ENTER:
            case keyCode.NUMPAD_ENTER:
                if(_self.panel.is(":visible"))
                    _self.items.filter('.ui-state-active').click();
                else
                    _self.show();
                break;
            
            case keyCode.ALT: 
            case 224:
                break;
            case keyCode.TAB:
                _self.hide();
            default:
                var letter = String.fromCharCode(e.keyCode).toLowerCase();

                if( _self.highlightKeyPath != letter ){

                    _self.highlightKeyPath += letter;
                    _self.highlightItems = [];
                    // find matches
                    for( var index = 0 ; index < _self.options.length; index++){
                        if(_self.options[index].text.toLowerCase().startsWith(_self.highlightKeyPath))
                            _self.highlightItems.push(_self.items.eq(index));
                    }
                }

                // no change
                if(_self.highlightItems.length < 1)
                    return;

                if(_self.highlightOption){

                    // similar
                    if($(_self.highlightOption).html().toLowerCase().startsWith(_self.highlightKeyPath)){
                        if(_self.highlightKeyPath.length < 2){
                            var i = 0;
                            for( ; i < _self.highlightItems.length && $(_self.highlightItems[i]).html() != $(_self.highlightOption).html(); i++);
                            _self.highlightIndex = i + 1;
                        }
                        else
                            return;
                    }
                    else{ // not similar

                        var o = _self.items.index(_self.highlightOption);
                        var n = _self.items.index(_self.highlightItems[0]);

                        // find nearest
                        for( var i = 0; i < _self.highlightItems.length && _self.items.index(_self.highlightItems[i]) < o ; i++);
                        _self.highlightIndex = i;
                    }
                }
                else{ // new
                    _self.highlightIndex = 0;
                }

                // round
                if(_self.highlightIndex == _self.highlightItems.length) {
                    _self.highlightIndex = 0;
                }

                _self.highlightOption = _self.highlightItems[_self.highlightIndex];
                _self.selectItem(_self.highlightOption);
        };

        e.preventDefault();
    });
}

/*
 * PrimeFaces.widget.SelectOneMenu.prototype.selectItem = function(item){
 * if(!item || !item.length || item.length == 0) return;
 * 
 * var yScrolled = this.panel.height() < this.itemContainer.height();
 * 
 * //closed panel if(this.panel.is(":hidden")) item.click(); else{
 * this.items.removeClass("ui-state-active"); item.addClass('ui-state-active'); //
 * check & align up/down overflow if(yScrolled){ var diff = item.offset().top +
 * item.outerHeight(true) - this.panel.offset().top; if( diff >
 * this.panel.height() ) this.panel.scrollTop(this.panel.scrollTop() + (diff -
 * this.panel.height())); else if( (diff -= item.outerHeight(true)*2 -
 * item.height()) < 0 ) this.panel.scrollTop( this.panel.scrollTop() + diff); } } }
 */

PrimeFaces.widget.SelectOneMenu.prototype.show = function() {
    // highlight current
    this.highlightItem(this.items.eq(this.options.filter(':selected').index()));
    
    // calculate panel position
    this.alignPanel();
    
    this.panel.css('z-index', ++PrimeFaces.zindex);
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
    }

    this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
}

PrimeFaces.widget.SelectOneMenu.prototype.hide = function() {
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '');
    }
    
    this.panel.css('z-index', '').hide();
}

PrimeFaces.widget.SelectOneMenu.prototype.disable = function() {
    this.disabled = true;
    this.jq.addClass('ui-state-disabled');
    this.labelContainer.attr("tabindex", -1);
}

PrimeFaces.widget.SelectOneMenu.prototype.enable = function() {
    this.disabled = false;
    this.jq.removeClass('ui-state-disabled');
    this.labelContainer.attr("tabindex", this.tabindex);
}

PrimeFaces.widget.SelectOneMenu.prototype.focus = function() {
    this.labelContainer.focus();
}

PrimeFaces.widget.SelectOneMenu.prototype.blur = function() {
    this.labelContainer.blur();
}

PrimeFaces.widget.SelectOneMenu.prototype.alignPanel = function() {
    this.panel.css({left:'', top:''})
    .position({
        my: 'left top'
        ,at: 'left bottom'
        ,of: this.jq
    });
}

/**
 * PrimeFaces SelectOneRadio Widget
 */
PrimeFaces.widget.SelectOneRadio = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.output = this.jq.find('.ui-radiobutton-box:not(.ui-state-disabled)');
    this.labels = this.jq.find('label:not(.ui-state-disabled)');
    this.icons = this.jq.find('.ui-radiobutton-icon');

    var _self = this;

    this.output.mouseover(function() {
        var radio = $(this);
        if(!radio.hasClass('ui-state-active'))
            $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    }).click(function() {
        var radio = $(this);
        if(!radio.hasClass('ui-state-active')) 
            _self.check($(this));
    });

    this.labels.click(function(e) {
        var input = $(PrimeFaces.escapeClientId($(this).attr('for'))),
        radio = input.parent().siblings('.ui-radiobutton-box');

        if(!radio.hasClass('ui-state-active'))
            _self.check(radio);
    });

    // Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectOneRadio, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectOneRadio.prototype.check = function(radio) {
    // unselect previous
    var previousRadio = this.output.filter('.ui-state-active'),
    previousInput = previousRadio.siblings('.ui-helper-hidden').children('input:radio');
    previousRadio.removeClass('ui-state-active').children('.ui-radiobutton-icon').removeClass('ui-icon ui-icon-bullet');
    previousInput.removeAttr('checked');

    // select current
    var input = radio.siblings('.ui-helper-hidden').children('input:radio');

    radio.addClass('ui-state-active');
    input.attr('checked', 'checked');
    radio.children('.ui-radiobutton-icon').addClass('ui-icon ui-icon-bullet');

    input.change();
}

/**
 * PrimeFaces SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanCheckbox = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');
    this.box = this.jq.find('.ui-chkbox-box');
    this.icon = this.box.children('.ui-chkbox-icon');
    this.itemLabel = this.jq.find('.ui-chkbox-label');
    this.disabled = this.input.is(':disabled');
    
    var _self = this;

    // bind events if not disabled
    if(!this.disabled) {
        this.box.mouseover(function() {
            _self.box.addClass('ui-state-hover');
        }).mouseout(function() {
            _self.box.removeClass('ui-state-hover');
        }).click(function() {
            _self.toggle();
        });
        
        // toggle state on label click
        this.itemLabel.click(function() {
            _self.toggle();
        });
        
        // Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
        }
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectBooleanCheckbox, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectBooleanCheckbox.prototype.toggle = function() {
    if(!this.disabled) {
        if(this.input.is(":checked"))
            this.uncheck();
        else
            this.check();
    }
}

PrimeFaces.widget.SelectBooleanCheckbox.prototype.check = function() {
    if(!this.disabled) {
        this.input.attr('checked', 'checked');
        this.box.addClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');

        this.input.change();
    }
}

PrimeFaces.widget.SelectBooleanCheckbox.prototype.uncheck = function() {
    if(!this.disabled) {
        this.input.removeAttr('checked');
        this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');

        this.input.change();
    }
}

/**
 * PrimeFaces SelectManyCheckbox Widget
 */
PrimeFaces.widget.SelectManyCheckbox = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);
    this.output = this.jq.find('.ui-chkbox-box:not(.ui-state-disabled)');
    this.labels = this.jq.find('label:not(.ui-state-disabled)');

    var _self = this;

    this.output.mouseover(function() {
        $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    }).click(function() {
        _self.toggle($(this));
    });

    this.labels.click(function(e) {
            e.preventDefault();
            var element = jQuery(this),
            input = jQuery(PrimeFaces.escapeClientId(element.attr('for'))),
            checkbox = input.parent().next();

            checkbox.click();
    });

    // Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectManyCheckbox, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectManyCheckbox.prototype.toggle = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        if(checkbox.hasClass('ui-state-active'))
            this.uncheck(checkbox);
        else
            this.check(checkbox);
    }
}

PrimeFaces.widget.SelectManyCheckbox.prototype.check = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
        checkbox.siblings('.ui-helper-hidden').children('input:checkbox').attr('checked', 'checked').change();
    }
}

PrimeFaces.widget.SelectManyCheckbox.prototype.uncheck = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
        checkbox.siblings('.ui-helper-hidden').children('input:checkbox').removeAttr('checked').change();
    }
}

/**
 * PrimeFaces SelectListbox Widget
 */
PrimeFaces.widget.SelectListbox = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');

    var listContainer = this.jq.children('ul'),
    options = $(this.input).children('option'),
    _self = this;

    // create elements for each option
    options.each(function(i) {
        var option = $(this),
        selected = option.is(':selected'),
        disabled = option.is(':disabled'),
        styleClass = 'ui-selectlistbox-item ui-corner-all';
        styleClass = disabled ? styleClass + ' ui-state-disabled' : styleClass;
        styleClass = selected ? styleClass + ' ui-state-active' : styleClass;
        
        listContainer.append('<li class="' + styleClass + '">' + option.text() + '</li>');
    });

    var items = listContainer.children('li:not(.ui-state-disabled)');

    items.mouseover(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active')) {
            $(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    }).click(function(e) {
        var element = $(this),
        option = $(options.get(element.index()));

        // clear previous selections
        if(_self.cfg.selection == 'single' || (_self.cfg.selection == 'multiple' && !e.metaKey)) {
            items.removeClass('ui-state-active ui-state-hover');
            options.removeAttr('selected');
        }
        
        if(_self.cfg.selection == 'multiple' && e.metaKey && element.hasClass('ui-state-active')) {
            element.removeClass('ui-state-active');
            option.removeAttr('selected');
        } 
        else {
            element.addClass('ui-state-active').removeClass('ui-state-hover');
            option.attr('selected', 'selected');
        }
        
        _self.input.change();
        
        PrimeFaces.clearSelection();
    });

    // Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

/*
 * PrimeFaces CommandButton Widget
 */
PrimeFaces.widget.CommandButton = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    
    PrimeFaces.skinButton(this.jq);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.CommandButton, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.CommandButton.prototype.disable = function() {
    this.jq.addClass('ui-state-disabled').attr('disabled', 'disabled');
}

PrimeFaces.widget.CommandButton.prototype.enable = function() {
    this.jq.removeClass('ui-state-disabled').removeAttr('disabled');
}

/*
 * PrimeFaces Button Widget
 */
PrimeFaces.widget.Button = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

	PrimeFaces.skinButton(this.jq);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Button, PrimeFaces.widget.BaseWidget);


(function($){
 	
	/**
	 * Set it up as an object under the jQuery namespace
	 */
	$.gritter = {};
	
	/**
	 * Set up global options that the user can over-ride
	 */
	$.gritter.options = {
		fade_in_speed: 'medium', // how fast notifications fade in
		fade_out_speed: 1000, // how fast the notices fade out
		time: 6000 // hang on the screen for...
	}
	
	/**
	 * Add a gritter notification to the screen
	 * 
	 * @see Gritter#add();
	 */
	$.gritter.add = function(params){

		try {
			return Gritter.add(params || {});
		} catch(e) {
		
			var err = 'Gritter Error: ' + e;
			(typeof(console) != 'undefined' && console.error) ? 
				console.error(err, params) : 
				alert(err);
				
		}
		
	}
	
	/**
	 * Remove a gritter notification from the screen
	 * 
	 * @see Gritter#removeSpecific();
	 */
	$.gritter.remove = function(id, params){
		Gritter.removeSpecific(id, params || {});
	}
	
	/**
	 * Remove all notifications
	 * 
	 * @see Gritter#stop();
	 */
	$.gritter.removeAll = function(params){
		Gritter.stop(params || {});
	}
	
	/**
	 * Big fat Gritter object
	 * 
	 * @constructor (not really since it's object literal)
	 */
	var Gritter = {
	    
		// Public - options to over-ride with $.gritter.options in "add"
		fade_in_speed: '',
		fade_out_speed: '',
		time: '',
	    
		// Private - no touchy the private parts
		_custom_timer: 0,
		_item_count: 0,
		_is_setup: 0,
		_tpl_close: '<div class="ui-growl-icon-close ui-icon ui-icon-closethick"></div>',
		_tpl_item: '<div id="gritter-item-[[number]]" class="ui-growl-item-container ui-widget-content ui-corner-all [[item_class]]" style="display:none"><div class="ui-growl-item">[[image]]<div class="[[class_name]]"><span class="ui-growl-title">[[username]]</span><p>[[text]]</p></div><div style="clear:both"></div></div></div>',
		_tpl_wrap: '<div class="ui-growl ui-widget"></div>',
	    
		/**
		 * Add a gritter notification to the screen
		 * 
		 * @param {Object}
		 *            params The object that contains all the options for
		 *            drawing the notification
		 * @return {Integer} The specific numeric id to that gritter
		 *         notification
		 */
		add: function(params){
		
			// Check the options and set them once
			if(!this._is_setup){
				this._runSetup();
			}
	        
			// Basics
			var user = params.title, 
				text = params.text,
				image = params.image || '',
				sticky = params.sticky || false,
				item_class = params.class_name || '',
				time_alive = params.time || '';
			
			this._verifyWrapper();
			
			this._item_count++;
			var number = this._item_count, 
				tmp = this._tpl_item;
			
			// Assign callbacks
			$(['before_open', 'after_open', 'before_close', 'after_close']).each(function(i, val){
				Gritter['_' + val + '_' + number] = ($.isFunction(params[val])) ? params[val] : function(){}
			});

			// Reset
			this._custom_timer = 0;
			
			// A custom fade time set
			if(time_alive){
				this._custom_timer = time_alive;
			}
			
			var image_str = (image != '') ? '<img src="' + image + '" class="ui-growl-image" />' : '',
			class_name = 'ui-growl-message';
			
			// String replacements on the template
			tmp = this._str_replace(
				['[[username]]', '[[text]]', '[[image]]', '[[number]]', '[[class_name]]', '[[item_class]]'],
				[user, text, image_str, this._item_count, class_name, item_class], tmp
			);
	        
			this['_before_open_' + number]();
			$('.ui-growl').append(tmp);
			
			var item = $('#gritter-item-' + this._item_count);
			
			item.fadeIn(this.fade_in_speed, function(){
				Gritter['_after_open_' + number]($(this));
			});
	        
			if(!sticky){
				this._setFadeTimer(item, number);
			}
			
			// Bind the hover/unhover states
			$(item).bind('mouseenter mouseleave', function(event){
				if(event.type == 'mouseenter'){
					if(!sticky){ 
						Gritter._restoreItemIfFading($(this), number);
					}
				}
				else {
					if(!sticky){
						Gritter._setFadeTimer($(this), number);
					}
				}
				Gritter._hoverState($(this), event.type);
			});
			
			return number;
	    
		},
		
		/**
		 * If we don't have any more gritter notifications, get rid of the
		 * wrapper using this check
		 * 
		 * @private
		 * @param {Integer}
		 *            unique_id The ID of the element that was just deleted, use
		 *            it for a callback
		 * @param {Object}
		 *            e The jQuery element that we're going to perform the
		 *            remove() action on
		 */
		_countRemoveWrapper: function(unique_id, e){
		    
			// Remove it then run the callback function
			e.remove();
			this['_after_close_' + unique_id](e);
			
			// Check if the wrapper is empty, if it is.. remove the wrapper
			if($('.ui-growl-item-container').length == 0){
				$('.ui-growl').remove();
			}
		
		},
		
		/**
		 * Fade out an element after it's been on the screen for x amount of
		 * time
		 * 
		 * @private
		 * @param {Object}
		 *            e The jQuery element to get rid of
		 * @param {Integer}
		 *            unique_id The id of the element to remove
		 * @param {Object}
		 *            params An optional list of params to set fade speeds etc.
		 * @param {Boolean}
		 *            unbind_events Unbind the mouseenter/mouseleave events if
		 *            they click (X)
		 */
		_fade: function(e, unique_id, params, unbind_events){
			
			var params = params || {},
				fade = (typeof(params.fade) != 'undefined') ? params.fade : true;
				fade_out_speed = params.speed || this.fade_out_speed;
			
			this['_before_close_' + unique_id](e);
			
			// If this is true, then we are coming from clicking the (X)
			if(unbind_events){
				e.unbind('mouseenter mouseleave');
			}
			
			// Fade it out or remove it
			if(fade){
			
				e.animate({
					opacity: 0
				}, fade_out_speed, function(){
					e.animate({height: 0}, 300, function(){
						Gritter._countRemoveWrapper(unique_id, e);
					})
				})
				
			}
			else {
				
				this._countRemoveWrapper(unique_id, e);
				
			}
					    
		},
		
		/**
		 * Perform actions based on the type of bind (mouseenter, mouseleave)
		 * 
		 * @private
		 * @param {Object}
		 *            e The jQuery element
		 * @param {String}
		 *            type The type of action we're performing: mouseenter or
		 *            mouseleave
		 */
		_hoverState: function(e, type){
			
			// Change the border styles and add the (X) close button when you
			// hover
			if(type == 'mouseenter'){
		    	
				e.addClass('hover');
				var find_img = e.find('img');
		    	
				// Insert the close button before what element
				(find_img.length) ? 
					find_img.before(this._tpl_close) : 
					e.find('span').before(this._tpl_close);
				
				// Clicking (X) makes the perdy thing close
				e.find('.ui-growl-icon-close').click(function(){
				
					var unique_id = e.attr('id').split('-')[2];
					Gritter.removeSpecific(unique_id, {}, e, true);
					
				});
			
			}
			// Remove the border styles and (X) close button when you mouse out
			else {
				
				e.removeClass('hover');
				e.find('.ui-growl-icon-close').remove();
				
			}
		    
		},
		
		/**
		 * Remove a specific notification based on an ID
		 * 
		 * @param {Integer}
		 *            unique_id The ID used to delete a specific notification
		 * @param {Object}
		 *            params A set of options passed in to determine how to get
		 *            rid of it
		 * @param {Object}
		 *            e The jQuery element that we're "fading" then removing
		 * @param {Boolean}
		 *            unbind_events If we clicked on the (X) we set this to true
		 *            to unbind mouseenter/mouseleave
		 */
		removeSpecific: function(unique_id, params, e, unbind_events){
			
			if(!e){
				var e = $('#gritter-item-' + unique_id);
			}
			
			// We set the fourth param to let the _fade function know to
			// unbind the "mouseleave" event. Once you click (X) there's no
			// going back!
			this._fade(e, unique_id, params || {}, unbind_events);
			
		},
		
		/**
		 * If the item is fading out and we hover over it, restore it!
		 * 
		 * @private
		 * @param {Object}
		 *            e The HTML element to remove
		 * @param {Integer}
		 *            unique_id The ID of the element
		 */
		_restoreItemIfFading: function(e, unique_id){
			
			clearTimeout(this['_int_id_' + unique_id]);
			e.stop().css({opacity: ''});
		    
		},
		
		/**
		 * Setup the global options - only once
		 * 
		 * @private
		 */
		_runSetup: function(){
		
			for(opt in $.gritter.options){
				this[opt] = $.gritter.options[opt];
			}
			this._is_setup = 1;
		    
		},
		
		/**
		 * Set the notification to fade out after a certain amount of time
		 * 
		 * @private
		 * @param {Object}
		 *            item The HTML element we're dealing with
		 * @param {Integer}
		 *            unique_id The ID of the element
		 */
		_setFadeTimer: function(e, unique_id){
			
			var timer_str = (this._custom_timer) ? this._custom_timer : this.time;
			this['_int_id_' + unique_id] = setTimeout(function(){ 
				Gritter._fade(e, unique_id); 
			}, timer_str);
		
		},
		
		/**
		 * Bring everything to a halt
		 * 
		 * @param {Object}
		 *            params A list of callback functions to pass when all
		 *            notifications are removed
		 */  
		stop: function(params){
			
			// callbacks (if passed)
			var before_close = ($.isFunction(params.before_close)) ? params.before_close : function(){};
			var after_close = ($.isFunction(params.after_close)) ? params.after_close : function(){};
			
			var wrap = $('.ui-growl');
			before_close(wrap);
			wrap.fadeOut(function(){
				$(this).remove();
				after_close();
			});
		
		},
		
		/**
		 * An extremely handy PHP function ported to JS, works well for
		 * templating
		 * 
		 * @private
		 * @param {String/Array}
		 *            search A list of things to search for
		 * @param {String/Array}
		 *            replace A list of things to replace the searches with
		 * @return {String} sa The output
		 */  
		_str_replace: function(search, replace, subject, count){
		
			var i = 0, j = 0, temp = '', repl = '', sl = 0, fl = 0,
				f = [].concat(search),
				r = [].concat(replace),
				s = subject,
				ra = r instanceof Array, sa = s instanceof Array;
			s = [].concat(s);
			
			if(count){
				this.window[count] = 0;
			}
		
			for(i = 0, sl = s.length; i < sl; i++){
				
				if(s[i] === ''){
					continue;
				}
				
		        for (j = 0, fl = f.length; j < fl; j++){
					
					temp = s[i] + '';
					repl = ra ? (r[j] !== undefined ? r[j] : '') : r[0];
					s[i] = (temp).split(f[j]).join(repl);
					
					if(count && s[i] !== temp){
						this.window[count] += (temp.length-s[i].length) / f[j].length;
					}
					
				}
			}
			
			return sa ? s : s[0];
		    
		},
		
		/**
		 * A check to make sure we have something to wrap our notices with
		 * 
		 * @private
		 */  
		_verifyWrapper: function(){
		  
			if($('.ui-growl').length == 0){
				$('body').append(this._tpl_wrap);
			}
		
		}
	    
	}
	
})(jQuery);

/**
 * PrimeFaces Growl Widget
 */
PrimeFaces.widget.Growl = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id
    this.jqId = PrimeFaces.escapeClientId(this.id);

    this.show(this.cfg.msgs);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Growl, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Growl.prototype.show = function(msgs) {
    if($('.ui-growl-item-container').length > 0) {
        $.gritter.removeAll({
            after_close:function() {
                $.each(msgs, function(index, msg) {
                    $.gritter.add(msg);
                });  
            }
        });
    } else {
        $.each(msgs, function(index, msg) {
            $.gritter.add(msg);
        })  
    }
    
}

PrimeFaces.widget.Growl.prototype.hideAll = function() {
    $.gritter.removeAll();
}

/**
	 * PrimeFaces Inplace Widget
	 */
PrimeFaces.widget.Inplace = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.display = $(this.jqId + '_display');
    this.content = $(this.jqId + '_content');
    this.cfg.formId = this.jq.parents('form:first').attr('id');
    this.onshowHandlers = [];

    var _self = this;
	
	if(!this.cfg.disabled) {
        
        if(this.cfg.toggleable) {
            this.display.bind(this.cfg.event, function(){
                _self.show();
            });

            this.display.mouseover(function(){
                $(this).toggleClass("ui-state-highlight");
            }).mouseout(function(){
                $(this).toggleClass("ui-state-highlight");
            });
        }
        else {
            this.display.css('cursor', 'default');
        }

        if(this.cfg.editor) {
            this.cfg.formId = $(this.jqId).parents('form:first').attr('id');

            this.editor = $(this.jqId + '_editor');
            
            var saveButton = this.editor.children('.ui-inplace-save'),
            cancelButton = this.editor.children('.ui-inplace-cancel');
            
            PrimeFaces.skinButton(saveButton).skinButton(cancelButton);

            saveButton.click(function(e) {_self.save(e)});
            cancelButton.click(function(e) {_self.cancel(e)});
        }
	}
    
    this.jq.data('widget', this);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Inplace, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Inplace.prototype.show = function() {    
    this.toggle(this.content, this.display, function() {
        this.content.find(':input:text:visible:enabled:first').focus().select();
    });
}

PrimeFaces.widget.Inplace.prototype.hide = function() {
    this.toggle(this.display, this.content);
}

PrimeFaces.widget.Inplace.prototype.toggle = function(elToShow, elToHide, callback) {
    var _self = this;

    if(this.cfg.effect == 'fade') {
        elToHide.fadeOut(this.cfg.effectSpeed,
            function(){
                elToShow.fadeIn(_self.cfg.effectSpeed);
                
                _self.postShow();
                
                if(callback)
                    callback.call(_self);
            });
    }
    else if(this.cfg.effect == 'slide') {
            elToHide.slideUp(this.cfg.effectSpeed,
                function(){
                    elToShow.slideDown(_self.cfg.effectSpeed);
                    
                    _self.postShow();
            });
    }
    else if(this.cfg.effect == 'none') {
            elToHide.hide();
            elToShow.show();
            
            _self.postShow();
    }
}

PrimeFaces.widget.Inplace.prototype.postShow = function() {
    // execute onshowHandlers and remove successful ones
    this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
		return !fn.call();
	});
}

PrimeFaces.widget.Inplace.prototype.getDisplay = function() {
    return this.display;
}

PrimeFaces.widget.Inplace.prototype.getContent = function() {
    return this.content;
}

PrimeFaces.widget.Inplace.prototype.save = function(e) {
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.hasBehavior('save')) {
        var saveBehavior = this.cfg.behaviors['save'];
        
        saveBehavior.call(this, e, options);
    } else {
        PrimeFaces.ajax.AjaxRequest(options); 
    }
}

PrimeFaces.widget.Inplace.prototype.cancel = function(e) {
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };
    
    var params = {};
    params[this.id + '_cancel'] = true;
    
    options.params = params;
    
    if(this.hasBehavior('cancel')) {
        var saveBehavior = this.cfg.behaviors['cancel'];
        
        saveBehavior.call(this, e, options);
    } else {
        PrimeFaces.ajax.AjaxRequest(options); 
    }
}

PrimeFaces.widget.Inplace.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

PrimeFaces.widget.Inplace.prototype.addOnshowHandler = function(fn) {
    this.onshowHandlers.push(fn);
};
/*
	 * Masked Input plugin for jQuery Copyright (c) 2007-2011 Josh Bush
	 * (digitalbush.com) Licensed under the MIT license
	 * (http://digitalbush.com/projects/masked-input-plugin/#license) Version:
	 * 1.3
	 */
(function($) {
	var pasteEventName = ($.browser.msie ? 'paste' : 'input') + ".mask";
	var iPhone = (window.orientation != undefined);

	$.mask = {
		// Predefined character definitions
		definitions: {
			'9': "[0-9]",
			'a': "[A-Za-z]",
			'*': "[A-Za-z0-9]"
		},
		dataName:"rawMaskFn"
	};

	$.fn.extend({
		// Helper Function for Caret positioning
		caret: function(begin, end) {
			if (this.length == 0) return;
			if (typeof begin == 'number') {
				end = (typeof end == 'number') ? end : begin;
				return this.each(function() {
					if (this.setSelectionRange) {
						this.setSelectionRange(begin, end);
					} else if (this.createTextRange) {
						var range = this.createTextRange();
						range.collapse(true);
						range.moveEnd('character', end);
						range.moveStart('character', begin);
						range.select();
					}
				});
			} else {
				if (this[0].setSelectionRange) {
					begin = this[0].selectionStart;
					end = this[0].selectionEnd;
				} else if (document.selection && document.selection.createRange) {
					var range = document.selection.createRange();
					begin = 0 - range.duplicate().moveStart('character', -100000);
					end = begin + range.text.length;
				}
				return { begin: begin, end: end };
			}
		},
		unmask: function() { return this.trigger("unmask"); },
		mask: function(mask, settings) {
			if (!mask && this.length > 0) {
				var input = $(this[0]);
				return input.data($.mask.dataName)();
			}
			settings = $.extend({
				placeholder: "_",
				completed: null
			}, settings);

			var defs = $.mask.definitions;
			var tests = [];
			var partialPosition = mask.length;
			var firstNonMaskPos = null;
			var len = mask.length;

			$.each(mask.split(""), function(i, c) {
				if (c == '?') {
					len--;
					partialPosition = i;
				} else if (defs[c]) {
					tests.push(new RegExp(defs[c]));
					if(firstNonMaskPos==null)
						firstNonMaskPos =  tests.length - 1;
				} else {
					tests.push(null);
				}
			});

			return this.trigger("unmask").each(function() {
				var input = $(this);
				var buffer = $.map(mask.split(""), function(c, i) { if (c != '?') return defs[c] ? settings.placeholder : c });
				var focusText = input.val();

				function seekNext(pos) {
					while (++pos <= len && !tests[pos]);
					return pos;
				};
				function seekPrev(pos) {
					while (--pos >= 0 && !tests[pos]);
					return pos;
				};

				function shiftL(begin,end) {
					if(begin<0)
					   return;
					for (var i = begin,j = seekNext(end); i < len; i++) {
						if (tests[i]) {
							if (j < len && tests[i].test(buffer[j])) {
								buffer[i] = buffer[j];
								buffer[j] = settings.placeholder;
							} else
								break;
							j = seekNext(j);
						}
					}
					writeBuffer();
					input.caret(Math.max(firstNonMaskPos, begin));
				};

				function shiftR(pos) {
					for (var i = pos, c = settings.placeholder; i < len; i++) {
						if (tests[i]) {
							var j = seekNext(i);
							var t = buffer[i];
							buffer[i] = c;
							if (j < len && tests[j].test(t))
								c = t;
							else
								break;
						}
					}
				};

				function keydownEvent(e) {
					var k=e.which;

					// backspace, delete, and escape get special treatment
					if(k == 8 || k == 46 || (iPhone && k == 127)){
						var pos = input.caret(),
							begin = pos.begin,
							end = pos.end;
						
						if(end-begin==0){
							begin=k!=46?seekPrev(begin):(end=seekNext(begin-1));
							end=k==46?seekNext(end):end;
						}
						clearBuffer(begin, end);
						shiftL(begin,end-1);

						return false;
					} else if (k == 27) {// escape
						input.val(focusText);
						input.caret(0, checkVal());
						return false;
					}
				};

				function keypressEvent(e) {
					var k = e.which,
						pos = input.caret();
					if (e.ctrlKey || e.altKey || e.metaKey || k<32) {// Ignore
						return true;
					} else if (k) {
						if(pos.end-pos.begin!=0){
							clearBuffer(pos.begin, pos.end);
							shiftL(pos.begin, pos.end-1);
						}

						var p = seekNext(pos.begin - 1);
						if (p < len) {
							var c = String.fromCharCode(k);
							if (tests[p].test(c)) {
								shiftR(p);
								buffer[p] = c;
								writeBuffer();
								var next = seekNext(p);
								input.caret(next);
								if (settings.completed && next >= len)
									settings.completed.call(input);
							}
						}
						return false;
					}
				};

				function clearBuffer(start, end) {
					for (var i = start; i < end && i < len; i++) {
						if (tests[i])
							buffer[i] = settings.placeholder;
					}
				};

				function writeBuffer() { return input.val(buffer.join('')).val(); };

				function checkVal(allow) {
					// try to place characters where they belong
					var test = input.val();
					var lastMatch = -1;
					for (var i = 0, pos = 0; i < len; i++) {
						if (tests[i]) {
							buffer[i] = settings.placeholder;
							while (pos++ < test.length) {
								var c = test.charAt(pos - 1);
								if (tests[i].test(c)) {
									buffer[i] = c;
									lastMatch = i;
									break;
								}
							}
							if (pos > test.length)
								break;
						} else if (buffer[i] == test.charAt(pos) && i!=partialPosition) {
							pos++;
							lastMatch = i;
						}
					}
					if (!allow && lastMatch + 1 < partialPosition) {
						input.val("");
						clearBuffer(0, len);
					} else if (allow || lastMatch + 1 >= partialPosition) {
						writeBuffer();
						if (!allow) input.val(input.val().substring(0, lastMatch + 1));
					}
					return (partialPosition ? i : firstNonMaskPos);
				};

				input.data($.mask.dataName,function(){
					return $.map(buffer, function(c, i) {
						return tests[i]&&c!=settings.placeholder ? c : null;
					}).join('');
				})

				if (!input.attr("readonly"))
					input
					.one("unmask", function() {
						input
							.unbind(".mask")
							.removeData($.mask.dataName);
					})
					.bind("focus.mask", function() {
						focusText = input.val();
						var pos = checkVal();
						writeBuffer();
						var moveCaret=function(){
							if (pos == mask.length)
								input.caret(0, pos);
							else
								input.caret(pos);
						};
						($.browser.msie ? moveCaret:function(){setTimeout(moveCaret,0)})();
					})
					.bind("blur.mask", function() {
						checkVal();
						if (input.val() != focusText)
							input.change();
					})
					.bind("keydown.mask", keydownEvent)
					.bind("keypress.mask", keypressEvent)
					.bind(pasteEventName, function() {
						setTimeout(function() { input.caret(checkVal(true)); }, 0);
					});

				checkVal(); // Perform initial check for existing values
			});
		}
	});
})(jQuery);

/*
 * PrimeFaces InputMask Widget
 */
PrimeFaces.widget.InputMask = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    this.jq.mask(this.cfg.mask, this.cfg);

    // Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    // Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.jq);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.InputMask, PrimeFaces.widget.BaseWidget);

/**
																				 * PrimeFaces
																				 * Menubar
																				 * Widget
																				 */
PrimeFaces.widget.Menubar = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.menuitems = this.jq.find('.ui-menuitem');

    this.bindEvents();

    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Menubar, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Menubar.prototype.bindEvents = function() {
    var _self = this;

    this.menuitems.mouseenter(function() {
        var menuitem = $(this),
        menuitemLink = menuitem.children('.ui-menuitem-link');

        if(!menuitemLink.hasClass('ui-state-disabled')) {
            menuitemLink.addClass('ui-state-hover');
        }

        var submenu = menuitem.children('ul.ui-menu-child');
        if(submenu.length == 1) {
            submenu.css('z-index', ++PrimeFaces.zindex);

            if(!menuitem.parent().hasClass('ui-menu-child')) {    // root
																	// menuitem
                submenu.css({
                    'left': 0
                    ,'top': menuitem.outerHeight()
                });
            } 
            else {                                              // submenu
																// menuitem
                submenu.css({
                    'left': menuitem.outerWidth()
                    ,'top': 0
                });
            }

            submenu.show();
        }
    })
    .mouseleave(function() {
        var menuitem = $(this),
        menuitemLink = menuitem.children('.ui-menuitem-link');

        menuitemLink.removeClass('ui-state-hover');

        menuitem.find('.ui-menu-child:visible').hide();
    })
    .click(function(e) {
        var menuitem = $(this);
        if(menuitem.children('.ui-menu-child').length == 0) {
            _self.jq.find('.ui-menu-child:visible').fadeOut('fast');
        }
        
        e.stopPropagation();
    });
}

/**
 * PrimeFaces Menu Widget
 */
PrimeFaces.widget.Menu = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.menuitems = this.jq.find('.ui-menuitem');
    this.cfg.tiered = this.cfg.type == 'tiered';
    this.cfg.sliding = this.cfg.type == 'sliding';

    var _self = this;
    
    // dynamic position
    if(this.cfg.position == 'dynamic') {        
        this.cfg.trigger = $(PrimeFaces.escapeClientId(this.cfg.trigger));
        
        /*
		 * we might have two menus with same ids if an ancestor of a menu is
		 * updated, if so remove the previous one and refresh jq
		 */
        if(this.jq.length > 1){
            $(document.body).children(this.jqId).remove();
            this.jq = $(this.jqId);
            this.jq.appendTo(document.body);
        }
        else if(this.jq.parent().is(':not(body)')) {
            this.jq.appendTo(document.body);
        }
        
        this.cfg.position = {
            my: this.cfg.my
            ,at: this.cfg.at
            ,of: this.cfg.trigger
        }
        
        this.cfg.trigger.bind(this.cfg.triggerEvent + '.ui-menu', function(e) {
            _self.jq.css({left:'', top:''}).position(_self.cfg.position);
            _self.show(e);
            
            // sliding rescue
            if(_self.cfg.sliding && !_self.slidingCfg.heighter.height()){
                _self.slidingCfg.heighter.css({height : _self.slidingCfg.rootList.height()});  
            }
        });
            
        // hide overlay when outside is clicked
        $(document.body).bind('click.ui-menu', function (e) {
            if(_self.jq.is(":hidden")) {
                return;
            }
            
            if(e.target === _self.cfg.trigger.get(0) || _self.cfg.trigger.find($(e.target)).length > 0) {
                return;
            }

            _self.hide();
        });
    }

    if(this.cfg.sliding){
        this.setupSliding();
    }

    // visuals
    this.bindEvents();
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Menu, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Menu.prototype.bindEvents = function() {  
    var _self = this;
    
    this.menuitems.mouseenter(function(e) {
        var menuitem = $(this),
        menuitemLink = menuitem.children('.ui-menuitem-link');

        if(menuitemLink.hasClass('ui-state-disabled')) {
            return false;
        }
        
        menuitemLink.addClass('ui-state-hover');
        
        if(_self.cfg.tiered) {
            var submenu = menuitem.children('ul.ui-menu-child');
            if(submenu.length == 1) {
                submenu.css({
                    'left': menuitem.outerWidth()
                    ,'top': 0
                    ,'z-index': ++PrimeFaces.zindex
                });

                submenu.show();
            }
        }
    }).mouseleave(function(e) {
        var menuitem = $(this),
        menuitemLink = menuitem.children('.ui-menuitem-link');
        
        menuitemLink.removeClass('ui-state-hover')
        
        if(_self.cfg.tiered) {
            menuitem.find('.ui-menu-child:visible').hide();
        }
    });
    
    if(this.cfg.tiered) {
        this.menuitems.click(function(e) {
            var menuitem = $(this);
            if(menuitem.children('.ui-menu-child').length == 0) {
                _self.jq.find('.ui-menu-child:visible').fadeOut('fast');
            }

            e.stopPropagation();
        });
    }
    else if(this.cfg.sliding){
        this.menuitems.click(function(e){
            if(_self.slidingCfg.animating)
                return;
            
            var menuitem = $(this),
            parents = menuitem.parents('ul.ui-menu-list').length,
            submenu = menuitem.children('ul.ui-menu-child');
            
            // invalid menuitem target
            if(submenu.length < 1||_self.slidingCfg.level!=parents-1)
                return;
            
            _self.slidingCfg.currentSubMenu = submenu.css({display : 'block'});
            _self.forward();
            
            e.stopPropagation();
       });
       
       this.slidingCfg.backButton.click(function(e){
           _self.backward();
           e.stopPropagation();
       });
    }
}

PrimeFaces.widget.Menu.prototype.setupSliding = function() {
    this.slidingCfg  = {};
    this.slidingCfg.scroll = this.jq.children('div.ui-menu-sliding-scroll:first');
    this.slidingCfg.state = this.slidingCfg.scroll.children('div.ui-menu-sliding-state:first');
    this.slidingCfg.wrapper = this.slidingCfg.state.children('div.ui-menu-sliding-wrapper:first');
    this.slidingCfg.content = this.slidingCfg.wrapper.children('div.ui-menu-sliding-content:first');
    this.slidingCfg.heighter = this.slidingCfg.content.children('div:first');
    this.slidingCfg.rootList = this.slidingCfg.heighter.children('ul:first');
    this.slidingCfg.backButton = this.jq.children('.ui-menu-backward');
    this.slidingCfg.easing= 'easeInOutCirc';
    this.slidingCfg.level = 0;
    
    var viewportWidth = this.jq.width(), viewportHeight = this.jq.height() - this.slidingCfg.backButton.height();
    this.slidingCfg.scroll.css({width : viewportWidth, height : viewportHeight});
    this.slidingCfg.state.css({width : viewportWidth, height : viewportHeight});
    this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width()});
    this.slidingCfg.rootList.find("ul.ui-menu-child").css({left : viewportWidth, width : viewportWidth - 18});
    this.slidingCfg.heighter.css({height : this.slidingCfg.rootList.height()});
    this.slidingCfg.width = viewportWidth;

    if(this.slidingCfg.wrapper.height() > this.slidingCfg.state.height())
        this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width() - 18});
    else
        this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width()});
}

PrimeFaces.widget.Menu.prototype.forward = function(){
    this.slide(++this.slidingCfg.level);
}

PrimeFaces.widget.Menu.prototype.backward = function(){
    if(!this.slidingCfg.level)
        return;
    
    var prev = this.slidingCfg.currentSubMenu, 
    back = function(){
        prev.css({display : 'none'});
    };
    
    this.slidingCfg.currentSubMenu = this.slidingCfg.currentSubMenu.parents('ul.ui-menu-list:first');
    this.slide(--this.slidingCfg.level, back);
}

PrimeFaces.widget.Menu.prototype.slide = function(level, fn){
    var _self = this, 
    currentHeight = _self.slidingCfg.currentSubMenu.outerHeight(true), 
    stateWidth = this.slidingCfg.state.width(),
    longer = currentHeight > this.slidingCfg.heighter.height();
    
    this.slidingCfg.animating = true;
    
    if(level == 0){
        this.slidingCfg.backButton.css({display : 'none'});
    }
    
    if(longer){
        _self.slidingCfg.heighter.height(currentHeight);
        var scrolled = this.slidingCfg.wrapper.height() > this.slidingCfg.state.height();
        if(scrolled){
            stateWidth = stateWidth - 18;
        }
    }
    
    if(currentHeight > this.slidingCfg.state.height()){
        this.slidingCfg.state.css({'overflow' : 'hidden', 'overflow-y' : 'auto'});
    }
    else{
        this.slidingCfg.state.css({'overflow' : 'hidden'});
    }
    
    this.slidingCfg.wrapper.css({width : stateWidth});
    _self.slidingCfg.state.scrollTop(0);
    
    this.slidingCfg.rootList.animate( 
    {
        left : -level * _self.slidingCfg.width
    },
    {
        easing: this.slidingCfg.easing,
        complete: function() {
            _self.slidingCfg.animating = false;
            
            if(!longer){
                _self.slidingCfg.heighter.height(currentHeight);
                var scrolled = _self.slidingCfg.wrapper.height() > _self.slidingCfg.state.height();
                if(scrolled){
                    stateWidth = _self.slidingCfg.state.width() - 18;
                }
                else{
                    stateWidth =  _self.slidingCfg.state.width();
                }
                _self.slidingCfg.wrapper.css({width : stateWidth});
            }
            
            _self.slidingCfg.currentSubMenu.css({width : stateWidth});

            if(fn) 
                fn.call();
            
            if(_self.slidingCfg.level > 0)
                _self.slidingCfg.backButton.css({display : 'block'});
        }
    });
}

PrimeFaces.widget.Menu.prototype.show = function(e) {
    this.jq.css('z-index', ++PrimeFaces.zindex).show();
    
    e.preventDefault();
}

PrimeFaces.widget.Menu.prototype.hide = function(e) {
    this.jq.fadeOut('fast');
}
            
/*
 * PrimeFaces MenuButton Widget
 */
PrimeFaces.widget.MenuButton = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.menuId = this.jqId + '_menu';
    this.jq = $(this.jqId);
    this.button = this.jq.children('button');
    this.menu = this.jq.children('.ui-menu');
    this.menuitems = this.jq.find('.ui-menuitem');
    this.cfg.disabled = this.button.is(':disabled');

    PrimeFaces.skinButton(this.button);

    if(!this.cfg.disabled) {
        this.bindEvents();

        $(document.body).children(this.menuId).remove();
        this.menu.appendTo(document.body);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.MenuButton, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.MenuButton.prototype.bindEvents = function() {  
    var _self = this;
    
    // menuitem visuals
    this.menuitems.mouseover(function(e) {
        var element = $(this);
        if(!element.hasClass('ui-state-disabled'))
            element.addClass('ui-state-hover');
        
    }).mouseout(function(e) {
        var element = $(this);
        element.removeClass('ui-state-hover');
    });
    
    this.cfg.position = {
        my: 'left top'
        ,at: 'left bottom'
        ,of: this.button
    }
    
    // button event
    this.button.click(function(e) {
        _self.menu.css({left:'', top:'','z-index': ++PrimeFaces.zindex}).position(_self.cfg.position);
        _self.menu.show();
    });
    
    // hide overlay when outside is clicked
    $(document.body).bind('click.ui-menubutton', function (e) {
        if(_self.jq.is(":hidden")) {
            return;
        }
        
        if(e.target === _self.button.get(0) || _self.button.find($(e.target)).length > 0) {
            return;
        }

        _self.menu.fadeOut('fast');
    });
}

/*
 * PrimeFaces ContextMenu Widget
 */
PrimeFaces.widget.ContextMenu = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.menuitems = this.jq.find('.ui-menuitem');
    var _self = this,
    documentTrigger = this.cfg.target === document;
        
    // trigger
    this.cfg.target = documentTrigger ? document : PrimeFaces.escapeClientId(this.cfg.target);
    var jqTarget = $(this.cfg.target);

    if(jqTarget.hasClass('ui-datatable')) {
        this.cfg.trigger = this.cfg.target + ' .ui-datatable-data tr';
    }
    else if(jqTarget.hasClass('ui-treetable')) {
        this.cfg.trigger = this.cfg.target + ' .ui-treetable-data ' + (this.cfg.nodeType ? 'tr.' + this.cfg.nodeType : 'tr');
    }
    else if(jqTarget.hasClass('ui-tree')) {
        this.cfg.trigger = this.cfg.target + ' ' + (this.cfg.nodeType ? 'li.' + this.cfg.nodeType + ' .ui-tree-node-content': '.ui-tree-node-content');
    }
    else {
        this.cfg.trigger = this.cfg.target;
    }
    
    // visuals
    this.bindEvents();
       
    // append to body
    this.jq.appendTo('body');
    
    // attach contextmenu
    if(documentTrigger) {
        $(this.cfg.trigger).bind('contextmenu.ui-contextmenu', function(e) {
            _self.show(e);
        });
    } 
    else {
        $(this.cfg.trigger).live('contextmenu.ui-contextmenu', function(e) {
            _self.show(e);
        });
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.ContextMenu, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.ContextMenu.prototype.bindEvents = function() {
    var _self = this;
    
    // menuitem visuals
    this.menuitems.mouseover(function(e) {
        var element = $(this);
        if(!element.hasClass('ui-state-disabled'))
            element.addClass('ui-state-hover');
        
    }).mouseout(function(e) {
        var element = $(this);
        element.removeClass('ui-state-hover');
    });
    
    // hide overlay when document is clicked
    $(document.body).bind('click.ui-contextmenu', function (e) {
        if(_self.jq.is(":hidden")) {
            return;
        }
        
        _self.hide();
    });
}

PrimeFaces.widget.ContextMenu.prototype.show = function(e) {  
    // hide other contextmenus if any
    $(document.body).children('.ui-contextmenu:visible').hide();

    var win = $(window),
    left = e.pageX,
    top = e.pageY,
    width = this.jq.outerWidth(),
    height = this.jq.outerHeight();

    // collision detection for window boundaries
    if((left + width) > (win.width())+ win.scrollLeft()) {
        left = left - width;
    }
    if((top + height ) > (win.height() + win.scrollTop())) {
        top = top - height;
    }

    this.jq.css({
        'left': left,
        'top': top,
        'z-index': ++PrimeFaces.zindex
    }).show();

    e.preventDefault();
}

PrimeFaces.widget.ContextMenu.prototype.hide = function(e) {
    this.jq.fadeOut('fast');
}

PrimeFaces.widget.ContextMenu.prototype.isVisible = function() {
    return this.jq.is(':visible');
} 

PrimeFaces.widget.NotificationBar = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
	this.jq = $(this.jqId);
    var _self = this;
	
    // relocate
	this.jq.css(this.cfg.position, '0').appendTo($('body'));

    // display initially
	if(this.cfg.autoDisplay) {
		$(this.jq).css('display','block')
    }
    
    // bind events
    this.jq.children('.ui-notificationbar-close').click(function() {
        _self.hide();
    });
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.NotificationBar, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.NotificationBar.prototype.show = function() {
	if(this.cfg.effect === 'slide')
		$(this.jq).slideDown(this.cfg.effect);
	else if(this.cfg.effect === 'fade')
		$(this.jq).fadeIn(this.cfg.effect);
	else if(this.cfg.effect === 'none')
		$(this.jq).show();
}

PrimeFaces.widget.NotificationBar.prototype.hide = function() {
	if(this.cfg.effect === 'slide')
		$(this.jq).slideUp(this.cfg.effect);
	else if(this.cfg.effect === 'fade')
		$(this.jq).fadeOut(this.cfg.effect);
	else if(this.cfg.effect === 'none')
		$(this.jq).hide();
}

/**
	 * PrimeFaces Panel Widget
	 */
PrimeFaces.widget.Panel = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);

    if(this.cfg.toggleable) {
        this.toggler = $(this.jqId + '_toggler');
        this.toggleStateHolder = $(this.jqId + '_collapsed');
        this.content = $(this.jqId + '_content');

        this.setupToggleTrigger();
    }

    if(this.cfg.closable) {
        this.visibleStateHolder = $(this.jqId + "_visible");

        this.setupCloseTrigger();
    }

    if(this.cfg.hasMenu) {
        this.visibleStateHolder = $(this.jqId + "_visible");

        this.setupMenuTrigger();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Panel, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Panel.prototype.toggle = function() {
    if(this.cfg.collapsed) {
        this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
        this.cfg.collapsed = false;
        this.toggleStateHolder.val(false);
    }
    else {
        this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');
        this.cfg.collapsed = true;
        this.toggleStateHolder.val(true);
    }
	
    var _self = this;

    this.content.slideToggle(this.cfg.toggleSpeed,
        function(e) {
            if(_self.cfg.behaviors) {
                var toggleBehavior = _self.cfg.behaviors['toggle'];
                if(toggleBehavior) {
                    toggleBehavior.call(_self, e);
                }
            }
        });
}

PrimeFaces.widget.Panel.prototype.close = function() {
    this.visibleStateHolder.val(false);

    var _self = this;

    $(this.jqId).fadeOut(this.cfg.closeSpeed,
        function(e) {
            if(_self.cfg.behaviors) {
                var closeBehavior = _self.cfg.behaviors['close'];
                if(closeBehavior) {
                    closeBehavior.call(_self, e);
                }
            }
        }
    );
}

PrimeFaces.widget.Panel.prototype.show = function() {
    $(this.jqId).fadeIn(this.cfg.closeSpeed);
	
    this.visibleStateHolder.val(true);
}

PrimeFaces.widget.Panel.prototype.setupToggleTrigger = function() {
    var _self = this,
    trigger = this.toggler.parent();

    this.setupTriggerVisuals(trigger);
    
    trigger.click(function() {_self.toggle();});
}

PrimeFaces.widget.Panel.prototype.setupCloseTrigger = function() {
    var _self = this,
    trigger = $(this.jqId + '_closer').parent();

    this.setupTriggerVisuals(trigger);
    
    trigger.click(function() {_self.close();});
}

PrimeFaces.widget.Panel.prototype.setupMenuTrigger = function() {
    var trigger = $(this.jqId + '_menu').parent();

    this.setupTriggerVisuals(trigger);
}

PrimeFaces.widget.Panel.prototype.setupTriggerVisuals = function(trigger) {
    trigger.mouseover(function() {$(this).addClass('ui-state-hover');})
            .mouseout(function() {$(this).removeClass('ui-state-hover');});
} 

PrimeFaces.widget.Poll = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.active = false;

    if(this.cfg.autoStart) {
        this.start();
    }
}

PrimeFaces.widget.Poll.prototype.start = function() {
    this.timer = setInterval(this.cfg.fn, (this.cfg.frequency * 1000));
    this.active = true;
}

PrimeFaces.widget.Poll.prototype.stop = function() {
    clearInterval(this.timer);
    this.active = false;
}

PrimeFaces.widget.Poll.prototype.handleComplete = function(xhr, status, args) {
    if(args.stop) {
        this.stop();
    }
}

PrimeFaces.widget.Poll.prototype.isActive = function() {
    return this.active;
}

/**
 * PrimeFaces OrderList Widget
 */
PrimeFaces.widget.OrderList = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.list = this.jq.find('.ui-orderlist-list'),
    this.items = this.list.children('.ui-orderlist-item');
    this.state = $(this.jqId + '_values');
    this.cfg.effect = this.cfg.effect||'fade';
    var _self = this;

    this.setupButtons();
    
    this.bindEvents();

    // Enable dnd
    this.list.sortable({
        revert: true,
        start: function(event, ui) {
            PrimeFaces.clearSelection();
        } 
        ,update: function(event, ui) {
            _self.onDragDrop(event, ui);
        }
    });
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.OrderList, PrimeFaces.widget.BaseWidget);

/**
 * Visuals
 */
PrimeFaces.widget.OrderList.prototype.bindEvents = function() {

    this.items.mouseover(function(e) {
        var element = $(this);

        if(!element.hasClass('ui-state-highlight'))
            $(this).addClass('ui-state-hover');
    })
    .mouseout(function(e) {
        var element = $(this);

        if(!element.hasClass('ui-state-highlight'))
            $(this).removeClass('ui-state-hover');
    })
    .mousedown(function(e) {
        var element = $(this);

        if(!e.metaKey) {
            element.removeClass('ui-state-hover').addClass('ui-state-highlight')
            .siblings('.ui-state-highlight').removeClass('ui-state-highlight');
        }
        else {
            if(element.hasClass('ui-state-highlight'))
                element.removeClass('ui-state-highlight');
            else
                element.removeClass('ui-state-hover').addClass('ui-state-highlight');
        }
    });
}

/**
 * Creates button controls using progressive enhancement
 */
PrimeFaces.widget.OrderList.prototype.setupButtons = function() {
    var _self = this;
    
    PrimeFaces.skinButton(this.jq.find('.ui-button'));
    
    this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-up').click(function() {_self.moveUp(_self.sourceList);});
    this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-top').click(function() {_self.moveTop(_self.sourceList);});
    this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-down').click(function() {_self.moveDown(_self.sourceList);});
    this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-bottom').click(function() {_self.moveBottom(_self.sourceList);});
}

PrimeFaces.widget.OrderList.prototype.onDragDrop = function(event, ui) {
    ui.item.removeClass('ui-state-highlight');

    this.saveState();
}

PrimeFaces.widget.OrderList.prototype.saveState = function() {
    var values = [];
    
    this.list.children('li.ui-orderlist-item').each(function() {
        values.push('"' + $(this).data('item-value') + '"');
    });
    
    this.state.val(values.join(','));
}

PrimeFaces.widget.OrderList.prototype.moveUp = function(list) {
    var _self = this;

    this.items.filter('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':first-child')) {
            item.hide(_self.cfg.effect, {}, 'fast', function() {
                item.insertBefore(item.prev()).show(_self.cfg.effect, {}, 'fast', function() {
                    _self.saveState();
                });
            });
        }
    });
}

PrimeFaces.widget.OrderList.prototype.moveTop = function(list) {
    var _self = this;

    this.items.filter('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':first-child')) {
            item.hide(_self.cfg.effect, {}, 'fast', function() {
                item.prependTo(item.parent()).show(_self.cfg.effect, {}, 'fast', function(){
                    _self.saveState();
                });
            });
        }

    });
}

PrimeFaces.widget.OrderList.prototype.moveDown = function(list) {
    var _self = this;

    this.items.filter('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':last-child')) {
            item.hide(_self.cfg.effect, {}, 'fast', function() {
                item.insertAfter(item.next()).show(_self.cfg.effect, {}, 'fast', function() {
                    _self.saveState();
                });
            });
        }

    });
}

PrimeFaces.widget.OrderList.prototype.moveBottom = function(list) {
    var _self = this;

    this.items.filter('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':last-child')) {
            item.hide(_self.cfg.effect, {}, 'fast', function() {
                item.appendTo(item.parent()).show(_self.cfg.effect, {}, 'fast', function() {
                    _self.saveState();
                });
            });
        }
    });
}

    PrimeFaces.widget.Paginator = function(cfg){
    this.cfg = cfg;
    this.jq = $();
    
    var _self = this;
    $.each(this.cfg.id, function(index, id){
        _self.jq = _self.jq.add($(PrimeFaces.escapeClientId(id)));
    });
    
    // elements
    this.pagesContainer = this.jq.children('.ui-paginator-pages');
    this.pageLinks = this.pagesContainer.children('.ui-paginator-page');
    this.rppSelect = this.jq.children('.ui-paginator-rpp-options');
    this.jtpSelect = this.jq.children('.ui-paginator-jtp-select');
    this.firstLink = this.jq.children('.ui-paginator-first');
    this.prevLink  = this.jq.children('.ui-paginator-prev');
    this.nextLink  = this.jq.children('.ui-paginator-next');
    this.endLink   = this.jq.children('.ui-paginator-last');
    this.currentReport = this.jq.children('.ui-paginator-current');
    
    // metadata
    this.cfg.rows = this.cfg.rows == 0 ? this.cfg.rowCount : this.cfg.rows;
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
    this.cfg.pageLinks = this.cfg.pageLinks||10;
    this.cfg.currentPageTemplate = this.cfg.currentPageTemplate||'({currentPage} of {totalPage})';
    
    // event bindings
    this.bindEvents();
}

PrimeFaces.widget.Paginator.prototype.bindEvents = function(){
    var _self = this;
    
    // visuals for first,prev,next,last buttons
    this.jq.children('span.ui-state-default').mouseover(function(){
        var item = $(this);
        if(!item.hasClass('ui-state-disabled')) {
            item.addClass('ui-state-hover');
        }
    }).mouseout(function(){
        $(this).removeClass('ui-state-hover');
    });
    
    // page links
    this.bindPageLinkEvents();
    
    // records per page selection
    this.rppSelect.change(function(e) {
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setRowsPerPage(parseInt($(this).val()));
        }
    });
    
    // jump to page
    this.jtpSelect.change(function(e) {
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(parseInt($(this).val()));
        }
    });
    
    // First page link
    this.firstLink.click(function() {
        PrimeFaces.clearSelection();
        
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(0);
        }
    });
    
    // Prev page link
    this.prevLink.click(function() {
        PrimeFaces.clearSelection();
        
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(_self.cfg.page - 1);
        }
    });
    
    // Next page link
    this.nextLink.click(function() {
        PrimeFaces.clearSelection();
        
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(_self.cfg.page + 1);
        }
    });
    
    // Last page link
    this.endLink.click(function() {
        PrimeFaces.clearSelection();
        
        if(!$(this).hasClass("ui-state-disabled")){
            _self.setPage(_self.cfg.pageCount - 1);
        }
    });
}

PrimeFaces.widget.Paginator.prototype.bindPageLinkEvents = function(){
    var _self = this;
    
    this.pagesContainer.children('.ui-paginator-page').bind('click', function(e){
        var link = $(this);

        if(!link.hasClass('ui-state-disabled')&&!link.hasClass('ui-state-active')) {
            _self.setPage(parseInt(link.text()) - 1);
        }
    }).mouseover(function(){
        var item = $(this);
        if(!item.hasClass('ui-state-disabled')&&!item.hasClass('ui-state-active')) {
            item.addClass('ui-state-hover');
        }
    }).mouseout(function(){
        $(this).removeClass('ui-state-hover');
    });
}

PrimeFaces.widget.Paginator.prototype.updateUI = function() {  
    // boundaries
    if(this.cfg.page == 0) {
        this.firstLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
        this.prevLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
    }
    else {
        this.firstLink.removeClass('ui-state-disabled');
        this.prevLink.removeClass('ui-state-disabled');
    }
    
    if(this.cfg.page == (this.cfg.pageCount - 1)){
        this.nextLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
        this.endLink.removeClass('ui-state-hover').addClass('ui-state-disabled');
    }
    else {
        this.nextLink.removeClass('ui-state-disabled');
        this.endLink.removeClass('ui-state-disabled');
    }
    
    // current page report
    var text = this.cfg.currentPageTemplate.replace('{currentPage}', this.cfg.page + 1).replace('{totalPage}', this.cfg.pageCount);
    this.currentReport.text(text);
    
    // dropdowns
    this.rppSelect.attr('value', this.cfg.rows);
    this.jtpSelect.attr('value', this.cfg.page);
    
    // page links
    this.updatePageLinks();
}

PrimeFaces.widget.Paginator.prototype.updatePageLinks = function() {
    var start, end, delta;
    
    // calculate visible page links
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
    var visiblePages = Math.min(this.cfg.pageLinks, this.cfg.pageCount);

    // calculate range, keep current in middle if necessary
    start = Math.max(0, Math.ceil(this.cfg.page - ((visiblePages) / 2)));
    end = Math.min(this.cfg.pageCount - 1, start + visiblePages - 1);
    
    // check when approaching to last page
    delta = this.cfg.pageLinks - (end - start + 1);
    start = Math.max(0, start - delta);

    // update dom
    this.pagesContainer.children().remove();
    for(var i = start; i <= end; i++) {
        var styleClass = 'ui-paginator-page ui-state-default ui-corner-all';
        if(this.cfg.page == i) {
            styleClass += " ui-state-active";
        }
        
        this.pagesContainer.append('<span class="' + styleClass + '">' + (i + 1) + '</span>')   
    }
    
    this.bindPageLinkEvents();
}

PrimeFaces.widget.Paginator.prototype.setPage = function(page, silent) {
    if(page >= 0 && page < this.cfg.pageCount && this.cfg.page != page){
        this.cfg.page = page;
        
        var newState = {
            first: this.cfg.rows * (this.cfg.page),
            rows: this.cfg.rows
        };

        if(!silent) {
            this.cfg.paginate.call(this, newState);
        }
    
        this.updateUI();
    }
}

PrimeFaces.widget.Paginator.prototype.setRowsPerPage = function(rpp) {
    var first = this.cfg.rows * this.cfg.page,
    page = parseInt(first / rpp);
    
    this.cfg.rows = rpp;
    
    this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows);
    
    this.cfg.page = -1;
    this.setPage(page);
}

PrimeFaces.widget.Paginator.prototype.setTotalRecords = function(value) {
    this.cfg.rowCount = value;
    this.cfg.pageCount = Math.ceil(value / this.cfg.rows)||1;
    this.cfg.page = 0;
    this.updateUI();
}

PrimeFaces.widget.Paginator.prototype.getCurrentPage = function() {
    return this.cfg.page;
}

/**
	 * PrimeFaces PickList Widget
	 */
PrimeFaces.widget.PickList = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.sourceList = this.jq.find('.ui-picklist-source');
    this.targetList = this.jq.find('.ui-picklist-target');
    this.sourceState = $(this.jqId + '_source');
    this.targetState = $(this.jqId + '_target');
    this.items = this.jq.find('.ui-picklist-item:not(.ui-state-disabled)');

    // Buttons
    this.setupButtons();

    if(this.cfg.disabled) {
        $(this.jqId + ' li.ui-picklist-item').addClass('ui-state-disabled');
        $(this.jqId + ' button').attr('disabled', 'disabled').addClass('ui-state-disabled');
    }
    else {
        var _self = this;

        // Sortable lists
        $(this.jqId + ' ul').sortable({
            cancel: '.ui-state-disabled',
            connectWith: this.jqId + ' .ui-picklist-list',
            revert: true,
            update: function(event, ui) {
                ui.item.removeClass('ui-state-highlight');
                
                _self.saveState();
            },
            receive: function(event, ui) {
                _self.fireOnTransferEvent(ui.item, ui.sender, ui.item.parents('ul.ui-picklist-list:first'), 'dragdrop');
            }
        });

        // Visual selection and Double click transfer
        this.items.mouseover(function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).addClass('ui-state-hover');
        })
        .mouseout(function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).removeClass('ui-state-hover');
        })
        .mousedown(function(e) {
            var element = $(this);
            
            if(!e.metaKey) {
                element.removeClass('ui-state-hover').addClass('ui-state-highlight')
                .siblings('.ui-state-highlight').removeClass('ui-state-highlight');
            }
            else {
                if(element.hasClass('ui-state-highlight'))
                    element.removeClass('ui-state-highlight');
                else
                    element.removeClass('ui-state-hover').addClass('ui-state-highlight');
            }
        })
        .dblclick(function() {
            var item = $(this);

            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                if($(this).parent().hasClass('ui-picklist-source'))
                    _self.transfer($(this), _self.sourceList, _self.targetList, 'dblclick');
                else
                    _self.transfer($(this), _self.targetList, _self.sourceList, 'dblclick');
            });

            PrimeFaces.clearSelection();
        });
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.PickList, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.PickList.prototype.setupButtons = function() {
    var _self = this;
        
    // visuals
    PrimeFaces.skinButton(this.jq.find('.ui-button'));
    
    // events
    $(this.jqId + ' .ui-picklist-button-add').click(function() {_self.add();});
    $(this.jqId + ' .ui-picklist-button-add-all').click(function() {_self.addAll();});
    $(this.jqId + ' .ui-picklist-button-remove').click(function() {_self.remove();});
    $(this.jqId + ' .ui-picklist-button-remove-all').click(function() {_self.removeAll();});

    if(this.cfg.showSourceControls) {
        $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.sourceList);});
        $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.sourceList);});
        $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.sourceList);});
        $(this.jqId + ' .ui-picklist-source-controls  .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.sourceList);});
    }

    if(this.cfg.showTargetControls) {
        $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.targetList);});
        $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.targetList);});
        $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.targetList);});
        $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.targetList);});
    }

}

PrimeFaces.widget.PickList.prototype.add = function() {
    var _self = this;

    this.sourceList.children('li.ui-picklist-item.ui-state-highlight').removeClass('ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer($(this), _self.sourceList, _self.targetList, 'command');
    });
}

PrimeFaces.widget.PickList.prototype.addAll = function() {
    var _self = this;

    this.sourceList.children('li.ui-picklist-item:not(.ui-state-disabled)').removeClass('ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer($(this), _self.sourceList, _self.targetList, 'command');
    });
}

PrimeFaces.widget.PickList.prototype.remove = function() {
    var _self = this;

    this.targetList.children('li.ui-picklist-item.ui-state-highlight').removeClass('ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer($(this), _self.targetList, _self.sourceList, 'command');
    });
}

PrimeFaces.widget.PickList.prototype.removeAll = function() {
    var _self = this;
    
    this.targetList.children('li.ui-picklist-item:not(.ui-state-disabled)').removeClass('ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer($(this), _self.targetList, _self.sourceList, 'command');
    });
}

PrimeFaces.widget.PickList.prototype.moveUp = function(list) {
    var _self = this;

    list.children('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':first-child')) {
            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                item.insertBefore(item.prev()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                    _self.saveState();
                });
            });
        }
    });
}

PrimeFaces.widget.PickList.prototype.moveTop = function(list) {
    var _self = this;

    list.children('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':first-child')) {
            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                item.prependTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function(){
                    _self.saveState();
                });
            });
        }

    });
}

PrimeFaces.widget.PickList.prototype.moveDown = function(list) {
    var _self = this;

    list.children('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':last-child')) {
            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                item.insertAfter(item.next()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                    _self.saveState();
                });
            });
        }

    });
}

PrimeFaces.widget.PickList.prototype.moveBottom = function(list) {
    var _self = this;

    list.children('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':last-child')) {
            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                item.appendTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                    _self.saveState();
                });
            });
        }

    });
}

PrimeFaces.widget.PickList.prototype.saveState = function() {
    this.saveListState(this.sourceList, this.sourceState);
    this.saveListState(this.targetList, this.targetState);
}

PrimeFaces.widget.PickList.prototype.transfer = function(item, from, to, type) {    
    var _self = this;
    
    item.appendTo(to).removeClass('ui-state-highlight').show(this.cfg.effect, {}, this.cfg.effectSpeed, function() {
        _self.saveState();
        _self.fireOnTransferEvent(item, from, to, type);
    });
}

PrimeFaces.widget.PickList.prototype.saveListState = function(list, holder) {
    var values = [];
    
    $(list).children('li.ui-picklist-item').each(function() {
        values.push('"' + $(this).data('item-value') + '"');
    });
    
    // set value as json string
    holder.val(values.join(','));
}

PrimeFaces.widget.PickList.prototype.fireOnTransferEvent = function(item, from, to, type) {
    if(this.cfg.onTransfer) {
        var obj = {};
        obj.item = item;
        obj.from = from;
        obj.to = to;
        obj.type = type;

        this.cfg.onTransfer.call(this, obj);
    }
} 

PrimeFaces.widget.ProgressBar = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    if(this.cfg.ajax) {
        this.cfg.formId = this.jq.parents('form:first').attr('id');
    }
	
    this.jq.progressbar(this.cfg);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.ProgressBar, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.ProgressBar.prototype.setValue = function(value) {
    this.jq.progressbar('value', value);
}

PrimeFaces.widget.ProgressBar.prototype.getValue  = function() {
    return this.jq.progressbar('value');
}

PrimeFaces.widget.ProgressBar.prototype.start = function() {
    var _self = this;
	
    if(this.cfg.ajax) {
		
        this.progressPoll = setInterval(function() {
            var options = {
                source: _self.id,
                process: _self.id,
                formId: _self.cfg._formId,
                async: true,
                oncomplete: function(xhr, status, args) {
                    var value = args[_self.id + '_value'];
                    _self.setValue(value);

                    // trigger close listener
                    if(value === 100) {
                        _self.fireCompleteEvent();
                    }
                }
            };

            PrimeFaces.ajax.AjaxRequest(options);
            
        }, this.cfg.interval);
    }
}

PrimeFaces.widget.ProgressBar.prototype.fireCompleteEvent = function() {
    clearInterval(this.progressPoll);

    if(this.cfg.behaviors) {
        var completeBehavior = this.cfg.behaviors['complete'];
        
        if(completeBehavior) {
            completeBehavior.call(this);
        }
    }
}

PrimeFaces.widget.ProgressBar.prototype.cancel = function() {
    clearInterval(this.progressPoll);
    this.setValue(0);
} 

PrimeFaces.widget.Resizable = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));

    if(this.cfg.ajaxResize) {
        this.cfg.formId = $(this.target).parents('form:first').attr('id');
    }

    var _self = this;

    this.cfg.stop = function(event, ui) {
        if(_self.cfg.onStop) {
            _self.cfg.onStop.call(_self, event, ui);
        }

        _self.fireAjaxResizeEvent(event, ui);
    }

    this.cfg.start = function(event, ui) {
        if(_self.cfg.onStart) {
            _self.cfg.onStart.call(_self, event, ui);
        }
    }
    
    this.cfg.resize = function(event, ui) {
        if(_self.cfg.onResize) {
            _self.cfg.onResize.call(_self, event, ui);
        }
    }

    this.jqTarget.resizable(this.cfg);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Resizable, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Resizable.prototype.fireAjaxResizeEvent = function(event, ui) {
    if(this.cfg.behaviors) {
        var resizeBehavior = this.cfg.behaviors['resize'];
        if(resizeBehavior) {
            var ext = {
                params:{}
            };
            ext.params[this.id + '_width'] = ui.helper.width();
            ext.params[this.id + '_height'] = ui.helper.height();

            resizeBehavior.call(this, event, ext);
        }
    }
}

/*
	 * PrimeFaces ScrollPanel Widget
	 */
PrimeFaces.widget.ScrollPanel = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    this.container = this.jq.children('.ui-scrollpanel-container');
    this.wrapper = this.container.children('.ui-scrollpanel-wrapper');
    this.content = this.wrapper.children('.ui-scrollpanel-content');
    
    var _self = this;
    
    if(this.jq.is(':visible')) {
        this.init();
    } 
    else {
        var hiddenParent = this.jq.parents('.ui-hidden-container:first'),
        hiddenParentWidget = hiddenParent.data('widget');
        
        if(hiddenParentWidget) {
            hiddenParentWidget.addOnshowHandler(function() {
                return _self.init();
            });
        }
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.ScrollPanel, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.ScrollPanel.prototype.init = function(){
    if(this.jq.is(':hidden'))
        return false;
    
    var contentWidth = this.content.outerWidth(true),
    contentHeight = this.content.outerHeight(true),
    
    containerWidth = this.jq.width(),
    containerHeight = this.jq.height();
    this.container.css({width: containerWidth, height: containerHeight});
    
    var xScrolled = contentWidth > containerWidth,
    yScrolled = contentHeight > containerHeight,
    hbar = this.container.children('.ui-scrollpanel-hbar'),
    vbar = this.container.children('.ui-scrollpanel-vbar'),

    wrapperWidth = containerWidth - (yScrolled ? vbar.width() : 0),
    wrapperHeight = containerHeight - (xScrolled ? hbar.height() : 0);
    this.wrapper.css({width: wrapperWidth, height: wrapperHeight});

    if(xScrolled){
        this.h = {
            bar  : hbar,
            hand : hbar.children('.ui-scrollpanel-handle'),
            grip : hbar.find('.ui-scrollpanel-handle > span.ui-icon-grip-solid-vertical'),
            up   : hbar.children('.ui-scrollpanel-bl'),
            down : hbar.children('.ui-scrollpanel-br'),
            wlen : wrapperWidth,
            diff : contentWidth - wrapperWidth,
            dir  : 'x'
        };

        this.initScroll(this.h);
    }

    if(yScrolled){
        this.v = {
            bar  : vbar,
            hand : vbar.children('.ui-scrollpanel-handle'),
            grip : vbar.find('.ui-scrollpanel-handle > span.ui-icon-grip-solid-horizontal'),
            up   : vbar.children('.ui-scrollpanel-bt'),
            down : vbar.children('.ui-scrollpanel-bb'),
            wlen : wrapperHeight,
            diff : contentHeight - wrapperHeight,
            dir  : 'y'
        };

        this.initScroll(this.v);
    }

    return true;
}

PrimeFaces.widget.ScrollPanel.prototype.initScroll = function(s){
    s.bar.css({display : 'block'});
    
    if(s.dir === 'x'){
        var barWidth = s.wlen - s.up.outerWidth(true) - s.down.outerWidth(true),
        scrollable = barWidth - s.hand.outerWidth(true);
        s.bar.css({width : barWidth});
        s.upLen = parseFloat(s.up.outerWidth(true));
        
        if( scrollable > s.diff){
            s.scrollable = s.diff;
            s.controller = s.diff;
            s.ratio = 1;
            s.hand.outerWidth((barWidth - s.diff));
            s.grip.css('margin-left', (s.hand.innerWidth() - s.grip.outerWidth(true))/2);
        }
        else{
            s.scrollable = scrollable;
            s.controller = scrollable;
            s.ratio = s.diff / scrollable;
        }
    }
    else{
        var barHeight = s.wlen - s.up.outerHeight(true) - s.down.outerHeight(true),
        scrollable = barHeight - s.hand.outerHeight(true);
        s.bar.css({height : barHeight});
        s.upLen = parseFloat(s.up.outerHeight(true));
        
        if( scrollable > s.diff){
            s.scrollable = s.diff;
            s.controller = s.diff;
            s.ratio = 1;
            s.hand.outerHeight((barHeight - s.diff));
            s.grip.css('margin-top', (s.hand.innerHeight() - s.grip.outerHeight(true))/2);
        }
        else{
            s.scrollable = scrollable;
            s.controller = scrollable;
            s.ratio = s.diff / scrollable;
        }
    }
    
    this.bindEvents(s);
}

PrimeFaces.widget.ScrollPanel.prototype.bindEvents = function(s){
    var scroll = s, _self = this;
    
    // visuals
    $.each([scroll.hand, scroll.up, scroll.down], function(i, e){
        e.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).mouseup(function() {
            $(this).removeClass('ui-state-active');
        }).mousedown(function() {
            $(this).addClass('ui-state-active');
        });
    });
    
    // wheel
    this.wrapper.bind("mousewheel",  function(event, move){ 
        if(_self.scrollWithRatio('y', move, true))
            event.preventDefault();
    });
    scroll.bar.bind("mousewheel",  function(event, move){ 
        _self.scrollWithRatio( scroll.dir, move, true);
        event.preventDefault();
    });
    
    // drag
    scroll.hand.draggable({
        axis: scroll.dir,
        drag: function (e, data) {
            if(scroll.dir === 'x'){
                var move = (e.target.offsetLeft - data.position.left);
                _self.scrollWithRatio('x', move);
            }
            else{
                var move = (e.target.offsetTop - data.position.top);
                _self.scrollWithRatio('y', move);
            }
        },
        containment: "parent",
        scroll: false,
        stop: function (e) {
            $(e.target).removeClass("ui-state-active");
        }
    });
    
    // buttons
    var mouseInterval, mouseDown = false, mouseCount = 0;
    scroll.up.mousedown(function(e){
        mouseDown = true;
        mouseCount = 0;
        mouseInterval = setInterval(function(){
            mouseCount++;
            _self.scrollWithRatio(scroll.dir, 2, true);
        }, 10);
        
        e.preventDefault();
    }).mouseenter(function(){
        if(mouseDown)
            $(this).mousedown();
    }).mouseup(function(){
        mouseDown = false;
        clearInterval(mouseInterval);
    }).mouseleave(function(){
        clearInterval(mouseInterval);
        $(this).removeClass('ui-state-active');
    }).click(function(){
        if(mouseCount < 5)
            _self.scrollWithRatio(scroll.dir, 20, true)
    });
    
    scroll.down.mousedown(function(e){
        mouseDown = true;
        mouseCount = 0;
        mouseInterval = setInterval(function(){
            mouseCount++;
            _self.scrollWithRatio(scroll.dir, -2, true);
        }, 10);
        
        e.preventDefault();
    }).mouseenter(function(){
        if(mouseDown)
            $(this).mousedown();
    }).mouseup(function(){
        mouseDown = false;
        clearInterval(mouseInterval);
    }).mouseleave(function(){
        clearInterval(mouseInterval);
        $(this).removeClass('ui-state-active');
    }).click(function(){
        if(mouseCount < 5)
            _self.scrollWithRatio(scroll.dir, -20, true)
    });
    
    $(document.body).bind('mouseup.scrollpanel', function(){
        clearInterval(mouseInterval);
        mouseDown = false;
    });
}

PrimeFaces.widget.ScrollPanel.prototype.scrollTo = function(x, y) {
    this.scrollX(x);
    this.scrollY(y);
}

PrimeFaces.widget.ScrollPanel.prototype.scrollToRatio = function(x, y, moveBars) {
    this.scrollWithRatio('x', x, moveBars === false ? false : true);
    this.scrollWithRatio('y', y, moveBars === false ? false : true);
}

PrimeFaces.widget.ScrollPanel.prototype.checkScrollable = function(o, d){
    if( o && d){
        if(o.controller + d < 0)
            return -o.controller;
        else if(o.controller + d > o.scrollable)
            return o.scrollable - o.controller;
        else
            return d;
    }
    return 0;
}

PrimeFaces.widget.ScrollPanel.prototype.scrollWithRatio = function(dir, d, wheel){
    if(dir === 'x'){
        d = this.checkScrollable(this.h, d);
        
        // invalid move
        if(!d) return false;
        
        this.h.controller += d;
        var scrolled = this.h.scrollable - this.h.controller,
        newLeft = -scrolled * this.h.ratio;
        
        this.content.css({left : newLeft});
        
        if(wheel){
            this.h.hand.css({left : this.h.upLen + scrolled});
        }
    }
    else{
        d = this.checkScrollable(this.v, d);
        
        // invalid move
        if(!d) return false;
        
        this.v.controller += d;
        var scrolled = this.v.scrollable - this.v.controller,
        newTop = -scrolled * this.v.ratio;
        
        this.content.css({top : newTop});
        
        if(wheel){
            this.v.hand.css({top : this.v.upLen + scrolled});
        }
    }
    
    return true;
}

PrimeFaces.widget.ScrollPanel.prototype.scrollX = function(x){
    this.content.css({left : typeof(x) == 'string' ? x : -x});
}

PrimeFaces.widget.ScrollPanel.prototype.scrollY = function(y){
    this.content.css({top : typeof(y) == 'string' ? y : -y});
}

/**
	 * PrimeFaces Slider Widget
	 */
PrimeFaces.widget.Slider = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
	this.input = $(PrimeFaces.escapeClientId(this.cfg.input));
	if(this.cfg.output) {
		this.output = $(PrimeFaces.escapeClientId(this.cfg.output));
	}
    var _self = this;
    
    // Create slider
	this.jq.slider(this.cfg);

    // Slide handler
	this.jq.bind('slide', function(event, ui) {
        _self.onSlide(event, ui);
    });

    // Slide start handler
    if(this.cfg.onSlideStart) {
        this.jq.bind('slidestart', function(event, ui) {_self.cfg.onSlideStart.call(this, event, ui);});
    }

    // Slide end handler
    this.jq.bind('slidestop', function(event, ui) {_self.onSlideEnd(event, ui);});
    
    this.input.keypress(function(e){
        var charCode = (e.which) ? e.which : e.keyCode
        if(charCode > 31 && (charCode < 48 || charCode > 57))
            return false;
        else
            return true;
    });
    
    this.input.keyup(function(){
      _self.setValue(_self.input.val());
    });
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Slider, PrimeFaces.widget.BaseWidget);
    
PrimeFaces.widget.Slider.prototype.onSlide = function(event, ui) {
    // User callback
    if(this.cfg.onSlide) {
        this.cfg.onSlide.call(this, event, ui);
    }

    // Update input and output(if defined)
	this.input.val(ui.value);
	
	if(this.output) {
		this.output.html(ui.value);
	}
}

PrimeFaces.widget.Slider.prototype.onSlideEnd = function(event, ui) {
    // User callback
    if(this.cfg.onSlideEnd) {
        this.cfg.onSlideEnd.call(this, event, ui);
    }

    if(this.cfg.behaviors) {
        var slideEndBehavior = this.cfg.behaviors['slideEnd'];

        if(slideEndBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_ajaxSlideValue'] = ui.value;

            slideEndBehavior.call(this, event, ext);
        }
    }
}

PrimeFaces.widget.Slider.prototype.getValue = function() {
    return this.jq.slider('value');
}

PrimeFaces.widget.Slider.prototype.setValue = function(value) {
    this.jq.slider('value', value);
}

PrimeFaces.widget.Slider.prototype.enable = function() {
    this.jq.slider('enable');
}

PrimeFaces.widget.Slider.prototype.disable = function() {
    this.jq.slider('disable');
}

/**
	 * PrimeFaces Spinner Widget
	 */
PrimeFaces.widget.Spinner = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.input = this.jq.children('.ui-spinner-input');
    this.upButton = this.jq.children('a.ui-spinner-up');
    this.downButton = this.jq.children('a.ui-spinner-down');
    this.decimalSeparator = this.findDecimalSeparator();
    this.decimalCount = this.findDecimalCount();
    var _self = this;
    
    // grab value from input
    this.refreshValue();

    if(this.cfg.disabled) {
        return;
    }

    // visuals
    this.jq.children('.ui-spinner-button')
        .mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).mouseup(function() {
            clearInterval(_self.timer);
            $(this).removeClass('ui-state-active');
        }).mousedown(function() {
            var element = $(this),
            dir = element.hasClass('ui-spinner-up') ? 1 : -1;

            element.addClass('ui-state-active');

            _self.repeat(null, dir);
        });

    // only allow numbers
    this.input.keypress(function (e){
        var charCode = (e.which) ? e.which : e.keyCode;

        if(charCode > 31 && (charCode < 48 || charCode > 57)) {
            return false;
        } else {
            return true;
        }
    });

    // refresh the value if user enters input manually
    this.input.keyup(function (e){
        _self.refreshValue();
    });

    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }

    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.input);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Spinner, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Spinner.prototype.repeat = function(interval, dir) {
    var _self = this,
    i = interval || 500;

    clearTimeout(this.timer);
    this.timer = setTimeout(function() {
        _self.repeat(40, dir);
    }, i);

    this.spin(this.cfg.step * dir);
}

PrimeFaces.widget.Spinner.prototype.spin = function(step) {
    var newValue = this.value + step;

    if(this.cfg.min != undefined && newValue < this.cfg.min) {
        newValue = this.cfg.min;
    }

    if(this.cfg.max != undefined && newValue > this.cfg.max) {
        newValue = this.cfg.max;
    }

    this.input.val(this.format(newValue));
    this.value = newValue;

    this.input.change();
}

PrimeFaces.widget.Spinner.prototype.refreshValue = function() {
    var value = this.input.val();
    
    if(value == '') {
        if(this.cfg.min)
            this.value = this.cfg.min;
        else
            this.value = 0;
    } 
    else {
        if(this.cfg.prefix)
            value = value.split(this.cfg.prefix)[1];

        if(this.cfg.suffix)
            value = value.split(this.cfg.suffix)[0];

        if(this.decimalSeparator)
            this.value =  parseFloat(value);
        else
            this.value = parseInt(value);
    }
}

PrimeFaces.widget.Spinner.prototype.findDecimalSeparator = function() {
    var step = this.cfg.step + '';

    if(step.indexOf('.') != -1) {
        return "."
    } else if(step.indexOf(',') != -1) {
        return ',';
    } else {
        return null;
    }
}

PrimeFaces.widget.Spinner.prototype.findDecimalCount = function() {
    var decimalSeparator = this.findDecimalSeparator(),
    step = this.cfg.step + '';

    if(decimalSeparator) {
        return step.split(decimalSeparator)[1].length;
    } else {
        return 0;
    }
}

PrimeFaces.widget.Spinner.prototype.format = function(value) {
    if(this.decimalSeparator) {
        value = value + '';

        var decimalCount = this.findDecimalCount(),
        valueDecimalCount = null;

        if(value.indexOf(this.decimalSeparator) != -1) {
            valueDecimalCount = value.split(this.decimalSeparator)[1].length;
        } else {
            valueDecimalCount = 0;
            value = value + this.decimalSeparator;
        }

        for(var i = valueDecimalCount ; i < decimalCount; i++) {
            value = value + '0';
        }
    }

    if(this.cfg.prefix)
        value = this.cfg.prefix + value;

    if(this.cfg.suffix)
        value = value + this.cfg.suffix;

    return value;
}

/**
	 * PrimeFaces TabView Widget
	 */
PrimeFaces.widget.TabView = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.navContainer = this.jq.children('.ui-tabs-nav');
    this.panelContainer = this.jq.children('.ui-tabs-panels');
    this.stateHolder = $(this.jqId + '_activeIndex');
    this.cfg.selected = parseInt(this.stateHolder.val());
    this.onshowHandlers = [];
    
    this.bindEvents();
	
    // Cache initial active tab
    if(this.cfg.dynamic && this.cfg.cache) {
        this.markAsLoaded(this.panelContainer.children().eq(this.cfg.selected));
    }
    
    this.jq.data('widget', this);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.TabView, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.TabView.prototype.bindEvents = function() {
    var _self = this;
    
    // Tab header events
    this.navContainer.children('li')
            .bind('mouseover.tabview', function(e) {
                var element = $(this);
                if(!element.hasClass('ui-state-disabled')) {
                    element.addClass('ui-state-hover');
                }
            })
            .bind('mouseout.tabview', function(e) {
                var element = $(this);
                if(!element.hasClass('ui-state-disabled')) {
                    element.removeClass('ui-state-hover');
                }
            })
            .bind('click.tabview', function(e) {
                var element = $(this);
                
                if($(e.target).is(':not(.ui-icon-close)')) {
                    var index = element.index();

                    if(!element.hasClass('ui-state-disabled') && index != _self.cfg.selected) {
                        _self.select(index);
                    }
                }
                
                e.preventDefault();
            });
            
    // Closable tabs
    this.navContainer.find('li .ui-icon-close')
        .bind('click.tabview', function(e) {
            _self.remove($(this).parent().index());
            
            e.preventDefault();
        });
}

/**
 * Selects an inactive tab given index
 */
PrimeFaces.widget.TabView.prototype.select = function(index) {
    // Call user onTabChange callback
    if(this.cfg.onTabChange) {
        var result = this.cfg.onTabChange.call(this, index);
        if(result == false)
            return false;
    }
    
    var newPanel = this.panelContainer.children().eq(index),
    shouldLoad = this.cfg.dynamic && !this.isLoaded(newPanel);
    
    // update state
    this.stateHolder.val(index);
    this.cfg.selected = index;
        
    if(shouldLoad) {
        this.loadDynamicTab(newPanel);
    }
    else {
        this.show(newPanel);

        this.fireTabChangeEvent(newPanel);
    }
    
    return true;
}

PrimeFaces.widget.TabView.prototype.show = function(newPanel) {
    var headers = this.navContainer.children(),
    oldHeader = headers.filter('.ui-state-active'),
    newHeader = headers.eq(newPanel.index()),
    oldPanel = this.panelContainer.children('.ui-tabs-panel:visible'),
    _self = this;
    
    if(this.cfg.effect) {
        oldPanel.hide(this.cfg.effect.name, null, this.cfg.effect.duration, function() {
            oldHeader.removeClass('ui-state-focus ui-tabs-selected ui-state-active');
            $(this).hide();
            
            newHeader.addClass('ui-state-focus ui-tabs-selected ui-state-active');
            newPanel.show(_self.cfg.effect.name, null, _self.cfg.effect.duration, function() {
                _self.postTabShow(newPanel);
            });
        });
    }
    else {
        oldHeader.removeClass('ui-state-focus ui-tabs-selected ui-state-active');
        oldPanel.hide();
        
        newHeader.addClass('ui-state-focus ui-tabs-selected ui-state-active');
        newPanel.show();
        
        this.postTabShow(newPanel);
    }
}

/**
 * Loads tab contents with ajax
 */
PrimeFaces.widget.TabView.prototype.loadDynamicTab = function(newPanel) {
    var _self = this,
    options = {
        source: this.id,
        process: this.id,
        update: this.id
    },
    tabindex = newPanel.index();

    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                newPanel.html(content);

                if(_self.cfg.cache) {
                    _self.markAsLoaded(newPanel);
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };
    
    options.oncomplete = function() {
        _self.show(newPanel);
    };


    var params = {};
    params[this.id + '_contentLoad'] = true;
    params[this.id + '_newTab'] = newPanel.attr('id');
    params[this.id + '_tabindex'] = tabindex;

    options.params = params;

    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'];
        
        tabChangeBehavior.call(this, newPanel, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

/**
 * Removes a tab with given index
 */
PrimeFaces.widget.TabView.prototype.remove = function(index) {    
    var header = this.navContainer.children().eq(index),
    panel = this.panelContainer.children().eq(index);
    
    this.fireTabCloseEvent(panel);
    
    header.remove();
    panel.remove();
    
    // active next tab if active tab is removed
    if(index == this.cfg.selected) {
        var newIndex = this.cfg.selected == this.getLength() ? this.cfg.selected - 1: this.cfg.selected;
        this.select(newIndex);
    }
}

PrimeFaces.widget.TabView.prototype.getLength = function() {
    return this.navContainer.children().length;
}

PrimeFaces.widget.TabView.prototype.getActiveIndex = function() {
    return this.cfg.selected;
}

PrimeFaces.widget.TabView.prototype.fireTabChangeEvent = function(panel) {
    var _self = this;
    
    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_newTab'] = panel.attr('id');
        ext.params[this.id + '_tabindex'] = panel.index();

        tabChangeBehavior.call(this, panel, ext);
    }
}

PrimeFaces.widget.TabView.prototype.fireTabCloseEvent = function(panel) {    
    if(this.hasBehavior('tabClose')) {
        var tabCloseBehavior = this.cfg.behaviors['tabClose'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_closeTab'] = panel.attr('id')
        ext.params[this.id + '_tabindex'] = panel.index();

        tabCloseBehavior.call(this, null, ext);
    }
}

PrimeFaces.widget.TabView.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

PrimeFaces.widget.TabView.prototype.markAsLoaded = function(panel) {
    panel.data('loaded', true);
}

PrimeFaces.widget.TabView.prototype.isLoaded = function(panel) {
    return panel.data('loaded') == true;
}

PrimeFaces.widget.TabView.prototype.disable = function(index) {
    this.navContainer.children().eq(index).addClass('ui-state-disabled');
}

PrimeFaces.widget.TabView.prototype.enable = function(index) {
    this.navContainer.children().eq(index).removeClass('ui-state-disabled');
}

PrimeFaces.widget.TabView.prototype.addOnshowHandler = function(fn) {
    this.onshowHandlers.push(fn);
}

PrimeFaces.widget.TabView.prototype.postTabShow = function(newPanel) {    
    // execute user defined callback
    if(this.cfg.onTabShow) {
        this.cfg.onTabShow.call(this, newPanel);
    }
    
    // execute onshowHandlers and remove successful ones
    this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
		return !fn.call();
	});
}

/**
	 * PrimeFaces TagCloud Widget
	 */
PrimeFaces.widget.TagCloud = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jq = $(PrimeFaces.escapeClientId(this.id));

    this.jq.find('li').mouseover(function() {
        $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    });
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.TagCloud, PrimeFaces.widget.BaseWidget);

/**
																			 * PrimeFaces
																			 * ThemeSwitcher
																			 * Widget
																			 */
PrimeFaces.widget.ThemeSwitcher = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.panelId = this.jqId + '_panel';
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');
    this.labelContainer = this.jq.find('.ui-selectonemenu-label-container');
    this.label = this.jq.find('.ui-selectonemenu-label');
    this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');
    this.triggers = this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');
    this.panel = this.jq.children(this.panelId);
    this.disabled = this.jq.hasClass('ui-state-disabled');
    
    // options
    if(!this.cfg.effectDuration) {
        this.cfg.effectDuration = 400;
    }
    
    // add selector
    this.jq.addClass('ui-themeswitcher');

    // visuals and behaviors
    this.bindEvents();

    // client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    // panel management
    $(document.body).children(this.panelId).remove();
    this.panel.appendTo(document.body);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.ThemeSwitcher, PrimeFaces.widget.BaseWidget);
        
PrimeFaces.widget.ThemeSwitcher.prototype.bindEvents = function() {

    var itemContainer = this.panel.children('.ui-selectonemenu-items'),
    items = itemContainer.find('.ui-selectonemenu-item'),
    options = $(this.input).children('option'),
    _self = this;

    // Events for items
    items.mouseover(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active')) {
            $(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active')) {
            $(this).removeClass('ui-state-hover');
        }
    }).click(function() {
        var element = $(this),
        option = $(options.get(element.index()));

        items.removeClass('ui-state-active ui-state-hover');
        element.addClass('ui-state-active');

        option.attr('selected', 'selected');

        _self.labelContainer.focus();
        _self.label.html(option.text());
        _self.input.change();
        _self.hide();
        
        // update theme
        PrimeFaces.changeTheme(option.attr('value'));
    });

    // Events to show/hide the panel
    this.triggers.mouseover(function() {
        if(!_self.disabled) {
            _self.triggers.addClass('ui-state-hover');
        }
    }).mouseout(function() {
        if(!_self.disabled) {
            _self.triggers.removeClass('ui-state-hover');
        }
    }).click(function(e) {

        if(!_self.disabled) {
            if(_self.panel.is(":hidden"))
                _self.show();
            else
                _self.hide();
        }

        e.preventDefault();
    });

    var offset;

    // hide overlay when outside is clicked
    $(document.body).bind('click', function (e) {
        if (_self.panel.is(":hidden")) {
            return;
        }
        offset = _self.panel.offset();
        if (e.target === _self.label.get(0) ||
            e.target === _self.menuIcon.get(0) ||
            e.target === _self.menuIcon.children().get(0)) {
            return;
        }
        if (e.pageX < offset.left ||
            e.pageX > offset.left + _self.panel.width() ||
            e.pageY < offset.top ||
            e.pageY > offset.top + _self.panel.height()) {
            _self.hide();
        }
        _self.hide();
    });

    this.labelContainer.focus(function(){
        if(!_self.disabled){
          _self.triggers.addClass('ui-state-focus');
          _self.menuIcon.addClass("ui-state-focus");
          _self.label.addClass("ui-state-focus");
        }
    }).blur(function(){
        _self.triggers.removeClass('ui-state-focus');
    });

}

PrimeFaces.widget.ThemeSwitcher.prototype.show = function() {
    this.alignPanel();

    this.panel.css('z-index', ++PrimeFaces.zindex);
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
    }

    this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
}

PrimeFaces.widget.ThemeSwitcher.prototype.hide = function() {
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '');
    }
    
    this.panel.css('z-index', '').hide();
}

PrimeFaces.widget.ThemeSwitcher.prototype.disable = function() {
    this.disabled = true;
    this.jq.addClass('ui-state-disabled');
}

PrimeFaces.widget.ThemeSwitcher.prototype.enable = function() {
    this.disabled = false;
    this.jq.removeclass('ui-state-disabled');
}

PrimeFaces.widget.ThemeSwitcher.prototype.alignPanel = function() {
    var offset = this.jq.offset(),
    panelWidth = this.panel.width(),
    buttonWidth = this.jq.width();
    
    this.panel.css({
       'top':  offset.top + this.jq.outerHeight(),
       'left': offset.left
    });
    
    if(panelWidth < buttonWidth) {
        this.panel.width(buttonWidth);
    }
}

/**
	 * PrimeFaces Tooltip Widget
	 */
PrimeFaces.widget.Tooltip = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    
    // remove previous element to support ajax updates
    $(document.body).children(this.jqId).remove();
    
    this.jq = $(this.jqId);
	this.cfg = cfg;
    this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
    
    // options
    this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent : 'mouseover';
    this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent : 'mouseout';
    this.cfg.showEffect = this.cfg.showEffect ? this.cfg.showEffect : 'fade';
    this.cfg.hideEffect = this.cfg.hideEffect ? this.cfg.hideEffect : 'fade';
    
    // bind tooltip to the target
    this.bindEvents();
	
	// append to body
    this.jq.appendTo(document.body);
    
    // use target title if value is blank
    if($.trim(this.jq.html()) == '') {
        this.jq.html(this.target.attr('title'));
    }
    
    // remove target's title
    this.target.removeAttr('title');

    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Tooltip, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Tooltip.prototype.bindEvents = function() {
    var _self = this;
    
    this.target.bind(this.cfg.showEvent, function() {
        _self.show();
    })
    .bind(this.cfg.hideEvent, function() {
        _self.hide();
    });
}

PrimeFaces.widget.Tooltip.prototype.show = function() {
    var _self = this;
    
    this.jq.css({
        left:'', 
        top:'',
        'z-index': ++PrimeFaces.zindex
    })
    .position({
        my: 'left top',
        at: 'right bottom',
        of: this.target
    });
    
    this.timeout = setTimeout(function() {
        _self.jq.show(_self.cfg.showEffect, {}, 400);
    }, 150);
}

PrimeFaces.widget.Tooltip.prototype.hide = function() {
    clearTimeout(this.timeout);
    
    this.jq.hide(this.cfg.hideEffect, {}, 400, function() {
        $(this).css('z-index', '');
    });
}

/**
	 * PrimeFaces Tree Widget
	 */
PrimeFaces.widget.Tree = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.cfg.formId = this.jq.parents('form:first').attr('id');

    if(this.cfg.selectionMode) {
        this.selectionHolder = $(this.jqId + '_selection');
        var selectionsValue = this.selectionHolder.val();
        this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

        if(this.cfg.selectionMode == 'checkbox')
            this.preselectCheckboxPropagation();
    }

    this.bindEvents();
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Tree, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Tree.prototype.bindEvents = function() {
    var _self = this,
    selectionMode = this.cfg.selectionMode;

    // expand-collapse
    $(this.jqId + ' .ui-tree-icon')
        .die()
        .live('click',function(e) {
            var icon = $(this),
            node = icon.parents('li:first');

            if(icon.hasClass('ui-icon-triangle-1-e'))
                _self.expandNode(node);
            else
                _self.collapseNode(node);
        });

    // selection hover
    if(selectionMode) {
        var clickTargetSelector = this.jqId  + ' .ui-tree-selectable-node';

        $(clickTargetSelector)
            .die('mouseover.tree mouseout.tree click.tree contextmenu.tree')
            .live('mouseover.tree', function() {
                var element = $(this);
                
                if(!element.hasClass('ui-state-highlight'))
                    $(this).addClass('ui-state-hover');
            })
            .live('mouseout.tree', function() {
                var element = $(this);
                
                if(!element.hasClass('ui-state-highlight'))
                    $(this).removeClass('ui-state-hover');
            })
            .live('click.tree', function(e) {
                _self.onNodeClick(e, $(this).parents('li:first'));
            })
            .live('contextmenu.tree', function(e) {
                _self.onNodeClick(e, $(this).parents('li:first'));
                e.preventDefault();
            });
    }
}

PrimeFaces.widget.Tree.prototype.onNodeClick = function(e, node) {
    PrimeFaces.clearSelection();
                    
    if($(e.target).is(':not(.ui-tree-icon)')) {
        if(this.cfg.onNodeClick) {
            this.cfg.onNodeClick.call(this, node);
        }
        
        if(this.isNodeSelected(node))
            this.unselectNode(e, node);
        else
            this.selectNode(e, node);
    }
}

PrimeFaces.widget.Tree.prototype.expandNode = function(node) {
    var _self = this;

    if(this.cfg.dynamic) {

        if(this.cfg.cache && node.children('.ui-tree-nodes').children().length > 0) {
            this.showNodeChildren(node);
            
            return;
        }

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId
        };

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();
                
                if(id == _self.id){
                    node.children('.ui-tree-nodes').append(content);
                    
                    _self.showNodeChildren(node);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }
            
            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };
        
        var params = {};
        params[this.id + '_expandNode'] = _self.getNodeId(node);

        options.params = params;

        if(this.hasBehavior('expand')) {
            var expandBehavior = this.cfg.behaviors['expand'];

            expandBehavior.call(this, node, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    }
    else {
        this.showNodeChildren(node);
        this.fireExpandEvent(node);
    }
}

PrimeFaces.widget.Tree.prototype.fireExpandEvent = function(node) {
    if(this.cfg.behaviors) {
        var expandBehavior = this.cfg.behaviors['expand'];
        if(expandBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_expandNode'] = this.getNodeId(node);
            
            expandBehavior.call(this, node, ext);
        }
    }
}

PrimeFaces.widget.Tree.prototype.collapseNode = function(node) {
    var _self = this,
    icon = node.find('.ui-tree-icon:first'),
    lastClass = node.attr('class').split(' ').slice(-1),
    nodeIcon = icon.next(),
    iconState = this.cfg.iconStates[lastClass];

    icon.addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');

    if(iconState) {
        nodeIcon.removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
    }

    var childNodeContainer = node.children('.ui-tree-nodes');
    childNodeContainer.hide();
        
    if(_self.cfg.dynamic && !_self.cfg.cache) {
        childNodeContainer.empty();
    }
    
    _self.fireCollapseEvent(node);
}

PrimeFaces.widget.Tree.prototype.fireCollapseEvent = function(node) {
    if(this.cfg.behaviors) {
        var collapseBehavior = this.cfg.behaviors['collapse'];
        if(collapseBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_collapseNode'] = this.getNodeId(node);
            
            collapseBehavior.call(this, node, ext);
        }
    }
}

PrimeFaces.widget.Tree.prototype.showNodeChildren = function(node) {
    var icon = node.find('.ui-tree-icon:first'),
    lastClass = node.attr('class').split(' ').slice(-1),
    nodeIcon = icon.next(),
    iconState = this.cfg.iconStates[lastClass];

    icon.addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');

    if(iconState) {
        nodeIcon.removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
    }

    node.children('.ui-tree-nodes').show();
}

PrimeFaces.widget.Tree.prototype.selectNode = function(e, node) {

    if(this.isCheckboxSelection()) {
        this.toggleCheckbox(node, true);
    }
    else {
        if(this.isSingleSelection() || (this.isMultipleSelection() && !e.metaKey)) {
            // clean all selections
            this.selections = [];
            this.jq.find('.ui-tree-node-content.ui-state-highlight').removeClass('ui-state-highlight');
        }

        node.find('.ui-tree-node-content:first').removeClass('ui-state-hover').addClass('ui-state-highlight');

        this.addToSelection(this.getNodeId(node));
    }

    this.writeSelections();

    this.fireNodeSelectEvent(node);
}

PrimeFaces.widget.Tree.prototype.unselectNode = function(e, node) {
    var nodeId = this.getNodeId(node);

    // select node
    if(this.isCheckboxSelection()) {
        this.toggleCheckbox(node, false);
        this.writeSelections();
        this.fireNodeUnselectEvent(node);
    }
    else if(e.metaKey) {
        // remove visual style
        node.find('.ui-tree-node-content:first').removeClass('ui-state-highlight');

        // remove from selection
        this.removeFromSelection(nodeId);
        
        this.writeSelections();

        this.fireNodeUnselectEvent(node);
    } 
    else if(this.isMultipleSelection()){
        this.selectNode(e, node);
    }
}

PrimeFaces.widget.Tree.prototype.writeSelections = function() {    
    this.selectionHolder.val(this.selections.join(','));
}

PrimeFaces.widget.Tree.prototype.fireNodeSelectEvent = function(node) {
    if(this.cfg.behaviors) {
        var selectBehavior = this.cfg.behaviors['select'];
        
        if(selectBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_instantSelection'] = this.getNodeId(node);
            
            selectBehavior.call(this, node, ext);
        }
    }
}

PrimeFaces.widget.Tree.prototype.fireNodeUnselectEvent = function(node) {
    if(this.cfg.behaviors) {
        var unselectBehavior = this.cfg.behaviors['unselect'];
        
        if(unselectBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_instantUnselection'] = this.getNodeId(node);
            
            unselectBehavior.call(this, node, ext);
        }
    }
}

PrimeFaces.widget.Tree.prototype.getNodeId = function(node) {
    return node.attr('id').split('_node_')[1];
}

PrimeFaces.widget.Tree.prototype.isNodeSelected = function(node) {
    return $.inArray(this.getNodeId(node), this.selections) != -1;
}

PrimeFaces.widget.Tree.prototype.isSingleSelection = function() {
    return this.cfg.selectionMode == 'single';
}

PrimeFaces.widget.Tree.prototype.isMultipleSelection = function() {
    return this.cfg.selectionMode == 'multiple';
}

PrimeFaces.widget.Tree.prototype.isCheckboxSelection = function() {
    return this.cfg.selectionMode == 'checkbox';
}

PrimeFaces.widget.Tree.prototype.addToSelection = function(nodeId) {
    this.selections.push(nodeId);
}

PrimeFaces.widget.Tree.prototype.removeFromSelection = function(nodeId) {
    this.selections = $.grep(this.selections, function(r) {
        return r != nodeId;
    });
}

PrimeFaces.widget.Tree.prototype.toggleCheckbox = function(node, check) {
    var _self = this;

    // propagate selection down
    node.find('.ui-tree-checkbox-icon').each(function() {
        var icon = $(this),
        nodeId = _self.getNodeId(icon.parents('li:first'));

        if(check) {
            if($.inArray(nodeId, _self.selections) == -1) {
                icon.addClass('ui-icon ui-icon-check');
            
                _self.addToSelection(nodeId);
            }
        }
        else {
            icon.removeClass('ui-icon ui-icon-check');

            _self.removeFromSelection(nodeId);
        }
    });

    // propagate selection up
    node.parents('li').each(function() {
        var parentNode = $(this),
        nodeId = _self.getNodeId(parentNode),
        icon = parentNode.find('.ui-tree-checkbox-icon:first'),
        checkedChildren = parentNode.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon.ui-icon-check'),
        allChildren = parentNode.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon');

        if(check) {
            if(checkedChildren.length == allChildren.length) {
                icon.removeClass('ui-icon ui-icon-minus').addClass('ui-icon ui-icon-check');

                _self.addToSelection(nodeId);
            } else {
                icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');
            }
        }
        else {
            if(checkedChildren.length > 0) {
                icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');
            } else {
                icon.removeClass('ui-icon ui-icon-minus');
            }

            _self.removeFromSelection(nodeId);
        }

    });
}

PrimeFaces.widget.Tree.prototype.preselectCheckboxPropagation = function() {
    this.jq.find('.ui-tree-checkbox-icon').not('.ui-icon-check').each(function() {
        var icon = $(this),
        node = icon.parents('li:first');

        if(node.find('.ui-tree-checkbox-icon.ui-icon-check').length > 0)
            $(this).addClass('ui-icon ui-icon-minus');
    });
}

PrimeFaces.widget.Tree.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

/**
	 * PrimeFaces TreeTable Widget
	 */
PrimeFaces.widget.TreeTable = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
	this.cfg = cfg;
    this.cfg.scrollable = this.jq.hasClass('ui-treetable-scrollable');
    this.cfg.resizable = this.jq.hasClass('ui-treetable-resizable');
    
    this.bindToggleEvents();
        
    // scrolling
    if(this.cfg.scrollable) {
        this.alignColumnWidths();
        this.setupScrolling();
    }

    // selection
    if(this.cfg.selectionMode) {
        this.jqSelection = $(this.jqId + '_selection');
        var selectionValue = this.jqSelection.val();
        
        this.selection = selectionValue === "" ? [] : selectionValue.split(',');
        
        this.bindSelectionEvents();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.TreeTable, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.TreeTable.prototype.bindToggleEvents = function() {
    var _self = this;
    
    // expand and collapse
    $(this.jqId + ' .ui-treetable-toggler').die('click.treetable')
        .live('click.treetable', function(e) {
            var toggler = $(this),
            node = toggler.parents('tr:first');
            
            if(toggler.hasClass('ui-icon-triangle-1-e'))
                _self.expandNode(e, node);
            else {
                _self.collapseNode(e, node);
            }
        });
}

PrimeFaces.widget.TreeTable.prototype.bindSelectionEvents = function() {
    var _self = this;
    
    $(this.jqId + ' .ui-treetable-data tr').die('mouseover.treetable mouseout.treetable click.treetable contextmenu.treetable')
            .live('mouseover.treetable', function(e) {
                var element = $(this);

                if(!element.hasClass('ui-selected')) {
                    element.addClass('ui-state-highlight');
                }
            })
            .live('mouseout.treetable', function(e) {
                var element = $(this);

                if(!element.hasClass('ui-selected')) {
                    element.removeClass('ui-state-highlight');
                }
            })
            .live('click.treetable', function(e) {
                _self.onRowClick(e, $(this));
                e.preventDefault();
            })            
            .live('contextmenu.treetable', function(event) {
               _self.onRowClick(event, $(this));
               event.preventDefault();
            });
}

PrimeFaces.widget.TreeTable.prototype.expandNode = function(e, node) {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id
    },
    _self = this,
    nodeKey = node.attr('id').split('_node_')[1];
    
    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                node.replaceWith(content);
                node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }

        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
        
        return true;
    };
    
    var params = {};
    params[this.id + '_expand'] = nodeKey;
    
    options.params = params;
    
    if(this.hasBehavior('expand')) {
        var expandBehavior = this.cfg.behaviors['expand'];
        
        expandBehavior.call(this, e, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

PrimeFaces.widget.TreeTable.prototype.collapseNode = function(e, node) {
    node.siblings('[id^="' + node.attr('id') + '"]').remove();

    node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
    
    if(this.hasBehavior('collapse')) {
        var collapseBehavior = this.cfg.behaviors['collapse'],
        nodeKey = node.attr('id').split('_node_')[1];
        
        var options = {
            params : {}
        };
        options.params[this.id + '_collapse'] = nodeKey;
        
        collapseBehavior.call(this, e, options);
    }
}

PrimeFaces.widget.TreeTable.prototype.onRowClick = function(e, node) {
    
    // Check if rowclick triggered this event not an element in row content
    if($(e.target).is('div.ui-tt-c,td')) {
        var selected = node.hasClass('ui-selected');

        if(selected)
            this.unselectNode(e, node);
        else
            this.selectNode(e, node);
    }
}

PrimeFaces.widget.TreeTable.prototype.selectNode = function(e, node) {
    var nodeKey = node.attr('id').split('_node_')[1];
    
    // unselect previous selection
    if(this.isSingleSelection() || (this.isMultipleSelection() && !e.metaKey)) {
        node.siblings('.ui-selected').removeClass('ui-selected ui-state-highlight'); 
        this.selection = [];
    }

    // add to selection
    node.addClass('ui-state-highlight ui-selected');
    this.addSelection(nodeKey);

    // save state
    this.writeSelections();
    
    this.fireSelectNodeEvent(e, nodeKey);
}

PrimeFaces.widget.TreeTable.prototype.unselectNode = function(e, node) {
    var nodeKey = node.attr('id').split('_node_')[1];

    if(e.metaKey) {
        // remove visual style
        node.removeClass('ui-selected ui-state-highlight');

        // remove from selection
        this.removeSelection(nodeKey);

        // save state
        this.writeSelections();
        
        this.fireUnselectNodeEvent(e, nodeKey);
    }
    else if(this.isMultipleSelection()){
        this.selectRow(e, node);
    }
}

PrimeFaces.widget.TreeTable.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

/**
 * Remove given rowIndex from selection
 */
PrimeFaces.widget.TreeTable.prototype.removeSelection = function(nodeKey) {
    var selection = this.selection;
    
    $.each(selection, function(index, value) {
        if(value === nodeKey) {
            selection.remove(index);
            
            return false;       // break
        } 
        else {
            return true;        // continue
        }
    });
}

/**
 * Adds given rowIndex to selection if it doesn't exist already
 */
PrimeFaces.widget.TreeTable.prototype.addSelection = function(nodeKey) {
    if(!this.isSelected(nodeKey)) {
        this.selection.push(nodeKey);
    }
}

PrimeFaces.widget.TreeTable.prototype.isSelected = function(nodeKey) {
    var selection = this.selection,
    selected = false;
    
    $.each(selection, function(index, value) {
        if(value === nodeKey) {
            selected = true;
            
            return false;       // break
        } 
        else {
            return true;        // continue
        }
    });
    
    return selected;
}

PrimeFaces.widget.TreeTable.prototype.isSingleSelection = function() {
    return this.cfg.selectionMode == 'single';
}

PrimeFaces.widget.TreeTable.prototype.isMultipleSelection = function() {
    return this.cfg.selectionMode == 'multiple';
}

/**
 * Writes selected row ids to state holder
 */
PrimeFaces.widget.TreeTable.prototype.writeSelections = function() {
    this.jqSelection.val(this.selection.join(','));
}

PrimeFaces.widget.TreeTable.prototype.fireSelectNodeEvent = function(e, nodeKey) {
    if(this.hasBehavior('select')) {
        var selectBehavior = this.cfg.behaviors['select'],
        options = {
            params : {}
        };
        
        options.params[this.id + '_instantSelect'] = nodeKey;
        
        selectBehavior.call(this, e, options);
    }
}

PrimeFaces.widget.TreeTable.prototype.fireUnselectNodeEvent = function(e, nodeKey) {
    if(this.hasBehavior('unselect')) {
        var unselectBehavior = this.cfg.behaviors['unselect'],
        options = {
            params : {}
        };
        
        options.params[this.id + '_instantUnselect'] = nodeKey;
        
        unselectBehavior.call(this, e, options);
    }
}

PrimeFaces.widget.TreeTable.prototype.setupScrolling = function() {
    var scrollHeader = $(this.jqId + ' .ui-treetable-scrollable-header'),
    scrollBody = $(this.jqId + ' .ui-treetable-scrollable-body'),
    scrollFooter = $(this.jqId + ' .ui-treetable-scrollable-footer');
    
    if(this.cfg.scrollWidth) {
        scrollHeader.width(this.cfg.scrollWidth);
        scrollBody.width(this.cfg.scrollWidth);
        scrollFooter.width(this.cfg.scrollWidth);
    }
        
    scrollBody.scroll(function() {
        scrollHeader.scrollLeft(scrollBody.scrollLeft());
        scrollFooter.scrollLeft(scrollBody.scrollLeft());
    });
}

/**
 * Moves widths of columns to column wrappers
 */
PrimeFaces.widget.TreeTable.prototype.alignColumnWidths = function() {
    this.jq.find('div.ui-tt-c').each(function() {
        var wrapper = $(this),
        column = wrapper.parent();
        
        wrapper.width(column.width());
        column.width('');
    });
} 

PrimeFaces.widget.Wizard = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.content = this.jqId + '_content';
    this.backNav = $(this.jqId + '_back');
    this.nextNav = $(this.jqId + '_next');
    this.cfg.formId = this.jq.parents('form:first').attr('id');
    var _self = this;
    
    this.currentStep = this.cfg.initialStep;
    var currentStepIndex = this.getStepIndex(this.currentStep);

    // Step controls
    if(this.cfg.showStepStatus) {
        this.stepControls = $(this.jqId + ' .ui-wizard-step-titles li.ui-wizard-step-title');
    }

    // Navigation controls
    if(this.cfg.showNavBar) {
        // visuals
        PrimeFaces.skinButton(this.backNav);
        PrimeFaces.skinButton(this.nextNav);
        
        // events
        this.backNav.click(function() {_self.back();});
        this.nextNav.click(function() {_self.next();});

        if(currentStepIndex == 0)
            this.backNav.hide();
        else if(currentStepIndex == this.cfg.steps.length - 1)
            this.nextNav.hide();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Wizard, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Wizard.prototype.back = function() {
    if(this.cfg.onback) {
        this.cfg.onback.call(this);
    }

    var stepToGo = this.cfg.steps[this.getStepIndex(this.currentStep) - 1];
	
    this.loadStep(stepToGo, true);
}

PrimeFaces.widget.Wizard.prototype.next = function() {
    if(this.cfg.onnext) {
        this.cfg.onnext.call(this);
    }

    var stepToGo = this.cfg.steps[this.getStepIndex(this.currentStep) + 1];
	
    this.loadStep(stepToGo, false);
}

PrimeFaces.widget.Wizard.prototype.loadStep = function(stepToGo, isBack) {
    var _self = this;

    var options = {
        source:this.id,
        process:this.id,
        update:this.id,
        formId:this.cfg.formId,
        onsuccess: function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find('update');

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            _self.currentStep = this.args.currentStep;

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    if(!this.args.validationFailed) {
                        // update content
                        $(_self.content).html(content);

                        // update navigation controls
                        var currentStepIndex = _self.getStepIndex(_self.currentStep);

                        if(_self.cfg.showNavBar) {
                             if(currentStepIndex == _self.cfg.steps.length - 1) {
                                _self.hideNextNav();
                                _self.showBackNav();
                            } else if(currentStepIndex == 0) {
                                _self.hideBackNav();
                                _self.showNextNav();
                            } else {
                                _self.showBackNav();
                                _self.showNextNav();
                            }
                        }

                        if(_self.cfg.showStepStatus) {
                            _self.stepControls.removeClass('ui-state-hover');
                            $(_self.stepControls.get(currentStepIndex)).addClass('ui-state-hover');
                        }

                    } else {
                        // update content
                        $(_self.content).html(content);
                    }

                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            return true;
        },
        error: function() {
            alert('Error in loading dynamic tab content');
        }
    };
    
    var params = {};
    params[this.id + '_wizardRequest'] = true;
    params[this.id + '_currentStep'] = this.currentStep;
    params[this.id + '_stepToGo'] = stepToGo;

    if(isBack) {
        params[this.id + '_backRequest'] = true;
    }

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.Wizard.prototype.getStepIndex = function(step) {
    for(var i=0; i < this.cfg.steps.length; i++) {
        if(this.cfg.steps[i] == step)
            return i;
    }
	
    return -1;
}

PrimeFaces.widget.Wizard.prototype.showNextNav = function() {
    this.nextNav.fadeIn();
}

PrimeFaces.widget.Wizard.prototype.hideNextNav = function() {
    this.nextNav.fadeOut();
}

PrimeFaces.widget.Wizard.prototype.showBackNav = function() {
    this.backNav.fadeIn();
}

PrimeFaces.widget.Wizard.prototype.hideBackNav = function() {
    this.backNav.fadeOut();
}
