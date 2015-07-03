checkWindow = function( formId ) {
	if( window.name == null || window.name == '' ) {
		if( document.getElementById( formId + ":windowId" ).value != '' && document.getElementById( formId + ":windowId" ).value == 0 ) {
			window.name = "aplosWindowId_" + 0;
		} else {
			var windowId = 0;
			if( $.cookie( "maxWindowId" ) == null || $.cookie( "maxWindowId" ) == '' ) {
				$.cookie("maxWindowId", windowId, { expires: 1, path : "/" } );	
			} else {
				windowId = parseInt( $.cookie( "maxWindowId" ) ) + 1;
				$.cookie( "maxWindowId", windowId, { expires: 1, path : "/" } );
			}

			window.name = "aplosWindowId_" + windowId;
			document.getElementById( formId + ":windowId" ).value = windowId;
			// Add querystring back to the action
			document.forms[ formId ].action = document.forms[ formId ].action + window.location.search; 
			document.forms[ formId ].submit();
		}
	} else if( document.getElementById( formId + ":windowId" ).value == null 
			|| document.getElementById( formId + ":windowId" ).value == ''
			|| document.getElementById( formId + ":windowId" ).value == 'null' ) {
		document.getElementById( formId + ":windowId" ).value = window.name.replace( "aplosWindowId_", "" );
		// Add querystring back to the action
		document.forms[ formId ].action = document.forms[ formId ].action + window.location.search;
		document.forms[ formId ].submit();
	}
};