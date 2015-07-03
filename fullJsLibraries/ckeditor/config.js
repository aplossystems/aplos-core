/*
Copyright (c) 2003-2010, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config )
{
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	config.toolbar = 'AploraToolbar';

	config.entities_processNumerical = true;
	config.entities_additional = "#39,#163,#233,#169,#8216,#8217,#8211,#8220,#8221";
	
	config.toolbar_AploraToolbar = 
		[
           ['Source','-','Save','NewPage','Preview','-','Templates'],
           ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Print', 'SpellChecker', 'Scayt'],
           ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
           ['Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField'],
           '/',
           ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
           ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
           ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
           ['Link','Unlink','Anchor'],
           ['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],
           '/',
       //    ['Styles','Format','Font','FontSize'],
       	   ['Styles','Format','FontSize'],
           ['TextColor','BGColor'],
           ['Maximize', 'ShowBlocks','-','About'],
           ['aploraFormsButton', 'aploraPagesButton']
       ];
	
	// protect <anytag class="preserve"></anytag>
	CKEDITOR.config.protectedSource.push( /<aplora:([\S]+)[^>]*[^>]*>.*<\/aplora:\1>/g );
	// protect <anytag class="preserve" /><
	CKEDITOR.config.protectedSource.push( /<aplora:[^>]+[^>\/]*\/>/g );
};
