package components;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;

import org.primefaces.model.UploadedFile;

import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.interfaces.FileUploaderInter;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

public class fileUploader2 extends UIInput implements NamingContainer, FileUploaderInter {
	
	/* REQUIRED */ public static final String FILE_IDENTIFIER = "key"; //Eg. 'publicity', string or Key object (enum)
	/* REQUIRED */ public static final String AFUI_IMPLEMENTATION_EL = "owner"; //The owning object for the file, the tag doesnt like us using parent
	/* OPTIONAL */ public static final String UPLOADED_FILE = "uploadedFile";
	/* OPTIONAL */ public static final String IS_SHOW_FILE_UPLOADER = "isShowFileUploader"; //TODO: Remove/Replace
	/* OPTIONAL */ public static final String AFUI_TYPES_EL = "types";
	/* OPTIONAL */ public static final String COLLECTION_KEY = "collectionKey";
	
	@Override
	public String getFamily() { return "javax.faces.NamingContainer"; }

	public fileUploader2() {
	}
	
	public void saveFileOwnerObject() {
		saveFileOwnerObject( true );
	}
	
	public boolean saveFileOwnerObject( boolean saveFileDetailsOwner ) {
		boolean savedSuccessfully = true;
		FileDetails determinedFileDetails = determineFileDetails();
		FileDetails saveableFileDetails = determinedFileDetails.getSaveableBean();
		FileDetailsOwnerInter fileDetailsOwner = determineFileDetailsOwner();
		saveableFileDetails.setFileDetailsOwner(fileDetailsOwner);
		determinedFileDetails.setShowFileUploading( true );
		if (CommonUtil.isFileUploaded(getUploadedFile())) {
			String ext = getUploadedFile().getFileName().substring( getUploadedFile().getFileName().lastIndexOf(".") + 1 );
			if ( isAcceptableType(ext) ) {
				innerSaveFileOwnerObject(saveableFileDetails, saveFileDetailsOwner);
			} else {
				JSFUtil.addMessageForError("Sorry, ." + ext + " is not an accepted type. Please upload only the following extensions " + CommonUtil.getStringOrEmpty(getAttributes().get( AFUI_TYPES_EL )) );
				fileDetailsOwner.getFileDetailsOwnerHelper().setFileDetails(null, getFileKey(), getAttributes().get( COLLECTION_KEY ) );
				savedSuccessfully = false;
			}
		}
		fileDetailsOwner.getFileDetailsOwnerHelper().saveCompleted(getFileKey(), getAttributes().get( COLLECTION_KEY ));
		determinedFileDetails.setShowFileUploading( false );
		return savedSuccessfully;
	}
	
	public void innerSaveFileOwnerObject( FileDetails fileDetails, boolean saveFileDetailsOwner ) {
		FileDetailsOwnerInter fileDetailsOwner = determineFileDetailsOwner();
		fileDetails.updateFile(getUploadedFile());
		moveFileIfTemporary();
		if( saveFileDetailsOwner ) {
			SaveableFileDetailsOwnerHelper saveableFileDetailsOwnerHelper = FileDetails.getSaveableFileDetailsOwner(fileDetailsOwner);
			if( saveableFileDetailsOwnerHelper != null ) {
				if (!saveableFileDetailsOwnerHelper.isNew()) {
					saveableFileDetailsOwnerHelper.saveDetails();
				}
			}
		}
		FileDetails determinedFileDetails = determineFileDetails();
		determinedFileDetails.setShowFileUploader( !fileDetails.fileExists() );
	}
	
	public boolean isAcceptableType(String ext) {
		String acceptableTypes = (String) getAttributes().get( AFUI_TYPES_EL );
		if ( acceptableTypes == null || acceptableTypes.equals("") ) {
			return true;
		} else if ( CommonUtil.isFileUploaded(getUploadedFile()) ) {
			String[] acceptableExtensions = acceptableTypes.split("[|,]");
			for ( String acceptableExtension : acceptableExtensions ) {
				if ( acceptableExtension.trim().toLowerCase().equals(ext.toLowerCase()) ) {
					return true;
				}
			}
		} 
		return false;
	}
	
	public void downloadFile() {
		FileDetails fileDetails = determineFileDetails();
		if (fileDetails != null) {
			fileDetails.setShowFileUploading( false );
			fileDetails.setUploadedFile( null );
			if (fileDetails.fileExists()) {
				fileDetails.setShowFileUploader( false );
				fileDetails.redirectToAplosUrl(true);
			} else {
				fileDetails.setShowFileUploader( true );
			}
		}
	}
	
	public void deleteBtnAction() {
		FileDetailsOwnerInter fileDetailsOwnerInter = determineFileDetailsOwner();
		SaveableFileDetailsOwnerHelper saveableFileDetailsOwnerHelper = FileDetails.getSaveableFileDetailsOwner(fileDetailsOwnerInter);
		if( saveableFileDetailsOwnerHelper != null ) {
			saveableFileDetailsOwnerHelper.setFileDetails( null, getFileKey(), getAttributes().get( COLLECTION_KEY ) );

			if( !saveableFileDetailsOwnerHelper.isNew() ) {
				saveableFileDetailsOwnerHelper.saveDetails();
			}
		}
	}

	public String getFileKey() {
		Object fileKey = getAttributes().get( FILE_IDENTIFIER );
		String fileIdentifier;
		if (fileKey instanceof Enum) {
			fileIdentifier = ((Enum) fileKey).name();
		} else if (fileKey instanceof Long){
			fileIdentifier = String.valueOf(fileKey);
		} else if (fileKey instanceof String){
			fileIdentifier = (String) fileKey;
		} else {
			fileIdentifier = fileKey.toString();
		}
		return fileIdentifier;
	}

	private String getFullIconUrl(String iconName) {
		//return MediaServlet.getImageUrl( iconName, AltruiWorkingDirectory.PROFILE_PICTURES.getDirectoryPath(), addContextPath );
		return "filetypes/" + iconName + ".png";
	}

	public String getFileDetailsName() {
		FileDetails fileDetails = determineFileDetails();
		if ( fileDetails != null && fileDetails.fileExists() ) {
			return fileDetails.getName();
		}
		return "";
	}
	
	public String getFileTypeIconUrl() {
		FileDetails fileDetails = determineFileDetails();
		if ( fileDetails != null && fileDetails.fileExists() ) {
			
			String url = fileDetails.getFilename();
			String extension = url.substring( url.lastIndexOf(".") + 1 ).toLowerCase();
			if (extension.contains("?")) {
				extension = extension.substring(0, extension.indexOf("?"));
			}
			if (extension.contains("&")) {
				extension = extension.substring(0, extension.indexOf("&"));
			}
			if ( extension.equals("pdf") ) {
				return getFullIconUrl("pdf");
			} else if ( extension.contains("doc") || extension.equals("txt") || extension.equals("rtf") ) {
				return getFullIconUrl("text");
			} else if ( extension.contains("mpg") || extension.contains("mpeg") || extension.equals("mp4") || extension.equals("mov") || extension.equals("wmv") || extension.contains("flv") || extension.contains("swf")) {
				return getFullIconUrl("movie");
			} else if ( extension.equals("mp3") || extension.equals("wav") ) {
				return getFullIconUrl("music");
			} else if ( extension.equals("bmp") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif") ) {
				return getFullIconUrl("picture");
			} else if ( extension.equals("exe") || extension.equals("msc") || extension.equals("msi") ) {
				return getFullIconUrl("application");
			} else if ( extension.equals("psd") || extension.equals("ai") || extension.equals("eps") || extension.equals("ps") ) {
				return getFullIconUrl("layer");
			} else if ( extension.equals("db") || extension.equals("sql") || extension.equals("csv") || extension.contains("xls") ) {
				return getFullIconUrl("database");
			}
		} 
		return getFullIconUrl("package");
	}
	
	public String getTypeText() {
		if ( determineFileDetails() != null && determineFileDetails().fileExists() ) {
			String url = determineFileDetails().getFilename();
			String extension = url.substring( url.lastIndexOf(".") + 1 ).toLowerCase();
			if (extension.contains("?")) {
				extension = extension.substring(0, extension.indexOf("?"));
			}
			if (extension.contains("&")) {
				extension = extension.substring(0, extension.indexOf("&"));
			}
			String fileCategory = "";
			/*if ( extension.equals("pdf") ) { //dont include pdf otherwise it reads ~PDF PDF
				fileCategory = " PDF";
			} else */ if ( extension.contains("log") || extension.contains("doc") || extension.equals("txt") || extension.equals("rtf") ) {
				fileCategory = " Text";
			} else if ( extension.contains("mpg") || extension.contains("mpeg") || extension.equals("mp4") || extension.equals("mov") || extension.equals("wmv") || extension.contains("flv") || extension.contains("swf") ) {
				fileCategory = " Movie";
			} else if ( extension.equals("mp3") || extension.equals("wav") ) {
				fileCategory = " Music";
			} else if ( extension.equals("bmp") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif") ) {
				fileCategory = " Picture";
			} else if ( extension.equals("exe") || extension.equals("msc") || extension.equals("msi") ) {
				fileCategory = " Application";
			} else if ( extension.equals("psd") || extension.equals("ai") || extension.equals("eps") || extension.equals("ps") ) {
				fileCategory = " Design";
			} else if ( extension.equals("xml") || extension.equals("db") || extension.equals("sql") || extension.equals("csv") || extension.contains("xls") ) {
				fileCategory = " Data";
			}
			return extension.toUpperCase() +  fileCategory;
		} 
		return "Unknown";
	}
	
//	public String getFileOwnerEl() {
//		String fileOwnerEl = getValueExpression( AFUI_IMPLEMENTATION_EL ).getExpressionString();
//		fileOwnerEl = fileOwnerEl.replace( "#{", "" ).replace( "}", "" );
//		return fileOwnerEl;
//	}

	//#####

	public UploadedFile getUploadedFile() {
		return determineFileDetails().getUploadedFile();
	}
	
	public void setUploadedFile( UploadedFile uploadedFile ) {
		determineFileDetails().setUploadedFile( uploadedFile );
	}
	
	public void cancelBtnAction() {
		FileDetails fileDetails = determineFileDetails();
		if( fileDetails != null ) {
			fileDetails.setShowFileUploading( false );
			fileDetails.setUploadedFile( null );
			if (fileDetails.fileExists()) {
				fileDetails.setShowFileUploader( false );
			} else {
				fileDetails.setShowFileUploader( true );
			}
		}
	}
	
	public boolean isShowFileUploader() {
		FileDetails fileDetails = determineFileDetails();
		return fileDetails == null || fileDetails.isShowFileUploader();
	}
	
	public FileDetailsOwnerInter determineFileDetailsOwner() {
		return (FileDetailsOwnerInter) getAttributes().get( AFUI_IMPLEMENTATION_EL );
	}
	
	public void moveFileIfTemporary() {
		FileDetailsOwnerInter fileDetailsOwner = determineFileDetailsOwner();
		FileDetails fileDetails = null;
		if (fileDetailsOwner != null) {
			if( fileDetailsOwner.getFileDetailsOwnerHelper().getFileDetails( getFileKey(), getAttributes().get( COLLECTION_KEY ) ) != null ) {
				return;
			}
		}

		if( fileDetails == null ) {
			fileDetails = fileDetailsOwner.getFileDetailsOwnerHelper().getTempFileDetails(getFileKey(), getAttributes().get( COLLECTION_KEY ));
			fileDetailsOwner.getFileDetailsOwnerHelper().setTempFileDetails( null, getFileKey(), getAttributes().get( COLLECTION_KEY ) );
			fileDetailsOwner.getFileDetailsOwnerHelper().setFileDetails( fileDetails, getFileKey(), getAttributes().get( COLLECTION_KEY ));
		}
	}
	
	public FileDetails determineFileDetails() {
		FileDetailsOwnerInter fileDetailsOwner = determineFileDetailsOwner();

		FileDetails fileDetails = null;
		if (fileDetailsOwner != null) {
			fileDetails = fileDetailsOwner.getFileDetailsOwnerHelper().getFileDetails( getFileKey(), getAttributes().get( COLLECTION_KEY ) );
		}
		if( fileDetails == null ) {
			fileDetails = fileDetailsOwner.getFileDetailsOwnerHelper().getTempFileDetails(getFileKey(), getAttributes().get( COLLECTION_KEY ));
		}
		if( fileDetails == null ) {
			fileDetails = fileDetailsOwner.getFileDetailsOwnerHelper().createCustomFileDetails( getFileKey() );
			if( fileDetails == null ) {
				fileDetails = new FileDetails( fileDetailsOwner, getFileKey() );
			}
			fileDetailsOwner.getFileDetailsOwnerHelper().setTempFileDetails(fileDetails, getFileKey(), getAttributes().get( COLLECTION_KEY ));
		}
		fileDetails.setFileUploader2(this);
		return fileDetails; //the fileDetailsOwnerInter probably doesnt evaluate to an object
	}
	
	public boolean isFileUploading() {
		FileDetails fileDetails = determineFileDetails();
		return fileDetails != null && determineFileDetails().isShowFileUploading();
	}
	
	
	
}
