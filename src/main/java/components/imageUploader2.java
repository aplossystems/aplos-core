package components;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;

import javax.media.jai.JAI;

import org.primefaces.model.UploadedFile;

import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.ResizedBufferedImage;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.ErrorEmailSender;
import com.aplos.common.utils.ImageUtil;
import com.aplos.common.utils.JSFUtil;
import com.sun.media.jai.codec.FileCacheSeekableStream;
import com.sun.media.jai.codec.SeekableStream;

public class imageUploader2 extends fileUploader2 {
	/* OPTIONAL */ public static final String RESIZE_WIDTH_ATTRIBUTE_KEY = "resizeWidth"; //the size to store the uploaded image
	/* OPTIONAL */ public static final String RESIZE_HEIGHT_ATTRIBUTE_KEY = "resizeHeight";
	/* OPTIONAL */ public static final String RESIZED_BUFFERED_IMAGE = "resizedBufferedImage";

	public imageUploader2() {
	}

	/**
	 * This method stores the resized image when called, to save each implementation
	 * rewriting the code
	 * @param filename
	 */
	public void makeSizedImage( FileDetails fileDetails ) {
		UploadedFile uploadedFile = getUploadedFile();
		if (CommonUtil.isFileUploaded(uploadedFile)) {
			try {
				// Use the JAI library as it accepts more formats than the standard ImageIO one.
				String fileFormat = ImageUtil.getFormatFromStream(uploadedFile.getInputstream());
				SeekableStream seekableStream =  new FileCacheSeekableStream(uploadedFile.getInputstream());
				String fileName = uploadedFile.getFileName();
				ParameterBlock pb = new ParameterBlock();
				pb.add(seekableStream);
				if( fileFormat == null ) {
					fileFormat = "jpg";
				}
				BufferedImage mainBufImage = JAI.create(fileFormat, pb).getAsBufferedImage();
				int effectiveWidth = mainBufImage.getWidth();
				int effectiveHeight = mainBufImage.getHeight();
				boolean isResizeRequired = false;
				if (determineResizeHeight() != null) {
					effectiveHeight = determineResizeHeight().intValue();
					isResizeRequired = true;
				}
				if (determineResizeWidth() != null) {
					effectiveWidth = determineResizeWidth().intValue();
					isResizeRequired = true;
				}

				if( isResizeRequired ) {
					mainBufImage = ImageUtil.resizeImage(mainBufImage, effectiveWidth, effectiveHeight);
				}

				ResizedBufferedImage resizedBufferedImage = new ResizedBufferedImage( mainBufImage, fileName, effectiveWidth, effectiveHeight);
				resizedBufferedImage.setImageResized(isResizeRequired);		
				resizedBufferedImage.setOriginalUploadedFile( uploadedFile );
				fileDetails.setResizedBufferedImage(resizedBufferedImage);
			} catch (IOException ioEx) {
				ErrorEmailSender.sendErrorEmail(JSFUtil.getRequest(),ApplicationUtil.getAplosContextListener(), ioEx);
				JSFUtil.addMessage("The color space of the image is not recognised, please resave the image through a program like photoshop or paint.");
				ioEx.printStackTrace();
			}
		}
	}

	
	@Override
	public void innerSaveFileOwnerObject( FileDetails fileDetails, boolean saveFileDetailsOwner ) {
		FileDetailsOwnerInter fileDetailsOwner = determineFileDetailsOwner();
		makeSizedImage( fileDetails );
		if( fileDetails.getResizedBufferedImage() == null ) {
			//form encoding type needs to be set if it is ignoring your image : enctype="multipart/form-data"
			JSFUtil.addMessageForError("Please select an image to upload.");
		} else {
			if( fileDetails.getResizedBufferedImage().isImageResized() ) {
				fileDetails.updateFile( fileDetails.getResizedBufferedImage() );
			} else {
				fileDetails.updateFile( getUploadedFile() );
			}
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
			determinedFileDetails.setResizedBufferedImage( null );
		}
	}

	public Long determineResizeHeight() {
		return ComponentUtil.determineLongAttributeValue( this, RESIZE_HEIGHT_ATTRIBUTE_KEY, null );
	}

	public Long determineResizeWidth() {
		return ComponentUtil.determineLongAttributeValue( this, RESIZE_WIDTH_ATTRIBUTE_KEY, null );
	}

	public String getImageUploaderBinding() {
		return getClientId().replace( ":", "_" ) + "_imageUploader";
	}
}
