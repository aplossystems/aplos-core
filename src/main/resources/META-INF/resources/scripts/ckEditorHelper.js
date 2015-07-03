
var ckEditorHelper = {

	createCKEditors : function( extraEditorOpts ) {
		if( extraEditorOpts != null && extraEditorOpts[ 'editorClassName' ] != null ) {
			ckEditorClass = extraEditorOpts['editorClassName'];
		} else {
			ckEditorClass = "aplos_ckeditor";
		}
		
		$j( "." + ckEditorClass ).each(function() {
			ckEditorHelper.createCKEditor( this, extraEditorOpts );
		});
	},
	
	addAdditionalSave : function( editorClassName ) {
		var additionalSave = function() { ckEditorHelper.updateEditors( { "editorClassName" : editorClassName } );ckEditorHelper.destroyEditors( { "editorClassName" : editorClassName } ) };
	
		additionalSaveFArray.push(additionalSave);
	}, 
	
	destroyEditors : function( opts ) {
		if( opts != null && opts[ 'editorClassName' ] != null ) {
			ckEditorClass = opts['editorClassName'];
		} else {
			ckEditorClass = "aplos_ckeditor";
		}
	
		$j( "." + ckEditorClass).each(function() {
			try {
				if (CKEDITOR.instances[this.id]) {
					CKEDITOR.instances[this.id].destroy(true);
				}
			} catch (e) {
				alert('destroyEditors: ' + e);
			}
		});
	},
	
	updateEditors : function( opts ) {
		if( opts != null && opts[ 'editorClassName' ] != null ) {
			ckEditorClass = opts['editorClassName'];
		} else {
			ckEditorClass = "aplos_ckeditor";
		}
	
		$j( "." + ckEditorClass).each(function() {
			if (CKEDITOR.instances[this.id]) {
				CKEDITOR.instances[this.id].updateElement();
				this.value = this.value.replace( /&nbsp;/g, "&#160;" );
			}
		});
	},
	
	createLimitedToolbarCKEditors : function( contextPath, extraEditorOpts, ckeditorClassName ) {
		this.setLimitedToolbar();
		var editorOptions = this.getDefaultEditorOptions( contextPath, extraEditorOpts );
		$j.extend( editorOptions, extraEditorOpts );
	
		if( ckeditorClassName == null ) {
			ckeditorClassName = ".aplos_ckeditor";
		} else {
			ckeditorClassName = "." + ckeditorClassName;
		}
	
		this.addProjectCkEditorStyles();
		var editors = new Array();
		var count = 0;
		$j(ckeditorClassName).each(function() {
			try {
				editors[ count++ ] = CKEDITOR.replace( this.id, editorOptions );
			} catch (e) {
				alert('createLimitedToolbarCKEditors: ' + e);
			}
		});
		return editors;
	},
	
	setLimitedToolbar : function() {
	
		CKEDITOR.config.toolbar = 'LimitedToolbar';			 
		CKEDITOR.config.toolbar_LimitedToolbar =
	    [
	        ['Source', '-', 'Styles','FontSize','TextColor'],
	        ['Bold','Italic','Underline','BulletedList'],
	        ['PasteText','PasteFromWord'],
	        ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
	        ['Link','Unlink','Anchor'],
	        ['Image','Table'],
	        ['Undo','Redo']
	    ];
	},
	
	setEmptyToolbar : function() {
	
		CKEDITOR.config.toolbar = 'EmptyToolbar';			 
		CKEDITOR.config.toolbar_EmptyToolbar =
	    [[]];
	},
	
	getDefaultEditorOptions : function( contextPath, extraEditorOpts ) {
		
		if ( contextPath != null ) {
			var windowId = window.name;
			if (windowId != null){
				windowId = windowId.substring("aplosWindowId_".length);
			}
			var aploraFilebrowserBrowseUrl = contextPath + "/common/browse.jsf?windowId=" + windowId;
			var aploraFilebrowserUploadUrl = contextPath + "/editorUpload/upload.jsf?directory=cmsPageRevision&windowId=" + windowId;
			if( extraEditorOpts != null && extraEditorOpts[ 'websiteId' ] != null ) {
				aploraFilebrowserUploadUrl = aploraFilebrowserUploadUrl + "&websiteId=" + extraEditorOpts[ 'websiteId' ];
			}
			var aploraFormsDialog = contextPath + "/scripts/ckeditor/aplora/dialog.js";
			var aploraPagesDialog = contextPath + "/scripts/ckeditor/aplora/dialog.js";
		}
		
		var editorOptions = { 
				filebrowserBrowseUrl : aploraFilebrowserBrowseUrl, 
				filebrowserUploadUrl : aploraFilebrowserUploadUrl,
				autoUpdateElement : true,
				height : "400",
				width : "700",
				enterMode : CKEDITOR.ENTER_BR,
				toolbar : CKEDITOR.config.toolbar_LimitedToolbar
		};
		jQuery.extend( editorOptions, extraEditorOpts );
		
		return editorOptions;
	},
	
	addProjectCkEditorStyles : function() {
		
	},
	
	createCKEditor : function( textAreaElement, editorOptions ) {
	
	//				var customToolbar = null;
	//				if( extraEditorOpts != null && extraEditorOpts[ 'customToolbar' ] != null ) {
	//					customToolbar = extraEditorOpts[ 'customToolbar' ];
	//				}
	//				
	//				if (customToolbar == null) {
	//					var editor = CKEDITOR.replace( textAreaElement.id,
	//						{
	//							filebrowserBrowseUrl : aploraFilebrowserBrowseUrl, 
	//							filebrowserUploadUrl : aploraFilebrowserUploadUrl,
	//							autoUpdateElement : true
	//						});
	//				}
	//				else {
	//					var editor = CKEDITOR.replace( textAreaElement.id,
	//						{
	//							filebrowserBrowseUrl : aploraFilebrowserBrowseUrl, 
	//							filebrowserUploadUrl : aploraFilebrowserUploadUrl,
	//							autoUpdateElement : true,
	//							toolbar : customToolbar
	//						});
	//				}
		
		var editor = CKEDITOR.replace( textAreaElement.id,editorOptions);
		return editor;
		
	
	//					editor.on('pluginsLoaded', function(ev) {
	//						if (!CKEDITOR.dialog.exists('aploraFormsDialog')) {
	//							CKEDITOR.dialog.add('aploraFormsDialog', aploraFormsDialog);
	//							CKEDITOR.dialog.add('aploraPagesDialog', aploraPagesDialog);
	//						}
	//
	//						editor.addCommand('aploraFormsCmd', new CKEDITOR.dialogCommand('aploraFormsDialog'));
	//
	//						editor.ui.addButton( 'aploraFormsButton', {
	//							label : 'Insert Form', command : 'aploraFormsCmd' }
	//						);
	//
	//						editor.addCommand('aploraPagesCmd', new CKEDITOR.dialogCommand('aploraPagesDialog'));
	//
	//						editor.ui.addButton( 'aploraPagesButton', {
	//							label : 'Link to Page', command : 'aploraPagesCmd' }
	//						);
	//					});
	}
}

  function createLimitedToolbarCKEditorsOlder( contextPath, extraEditorOpts, ckeditorClassName ) {
	if( contextPath != null ) {
		var aploraFilebrowserBrowseUrl = contextPath + "/common/browse.jsf";
		var aploraFilebrowserUploadUrl = contextPath + "/editorUpload/upload.jsf?directory=cmsPageRevision";
		var aploraFormsDialog = contextPath + "/scripts/ckeditor/aplora/dialog.js";
		var aploraPagesDialog = contextPath + "/scripts/ckeditor/aplora/dialog.js";
	}

	CKEDITOR.config.toolbar = 'LimitedToolbar';			 
	CKEDITOR.config.toolbar_LimitedToolbar =
    [
        ['Source', '-', 'Styles','FontSize','TextColor'],
        ['Bold','Italic','Underline','BulletedList'],
        ['PasteText','PasteFromWord'],
        ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
        ['Link','Unlink','Anchor'],
        ['Image','Table'],
        ['Undo','Redo']
    ];	

	var editorOptions = { 
			filebrowserBrowseUrl : aploraFilebrowserBrowseUrl, 
			filebrowserUploadUrl : aploraFilebrowserUploadUrl,
			autoUpdateElement : true,
			height : "400",
			width : "700",
			toolbar : CKEDITOR.config.toolbar_LimitedToolbar
	};

	$j.extend( editorOptions, extraEditorOpts );

	if( ckeditorClassName == null ) {
		ckeditorClassName = ".aplos_ckeditor";
	} else {
		ckeditorClassName = "." + ckeditorClassName;
	}

	$j(ckeditorClassName).each(function() {
		try {
			var editor = CKEDITOR.replace( this.id, editorOptions );
		} catch (e) {
			alert('createLimitedToolbarCKEditors: ' + e);
		}
	});
}

aplosJavascript.componentAvailable('ckEditorHelper');