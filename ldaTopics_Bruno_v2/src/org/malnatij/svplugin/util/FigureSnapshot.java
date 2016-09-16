package org.malnatij.svplugin.util;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.SnapShotAction;
import org.malnatij.svplugin.views.actions.dialogs.SnapshotWarning;

public class FigureSnapshot extends Thread {
	public static final String FILE_EXTENSION = ".png";
	private SnapShotAction snapShotAction;
	private XRayWorkbenchView viewContainer;
	private int format;
	private String fileName;
	private Device device;
	private Rectangle r;
	
	/**
	 * Saves the bytes of an encoded image for the specified
	 * IFigure in the specified format.
	 *
	 * @param figure the Figure to create an image for.
	 * @param format one of SWT.IMAGE_BMP, SWT.IMAGE_BMP_RLE, SWT.IMAGE_GIF
	 *          SWT.IMAGE_ICO, SWT.IMAGE_JPEG, or SWT.IMAGE_PNG
	 */
	private void createImage(IFigure figure,
			int format,
			String fileName) {
		this.format = format;
		this.fileName = fileName;
		
	    device = Display.getCurrent();
	    r = figure.getBounds();
	    
	    if(r.width * r.height > 1200 * 1200){
	    	new SnapshotWarning(this, viewContainer, snapShotAction);
	    } else {
	    	saveImage(figure);
	    }

	}
	
	public void saveImage(IFigure figure){
		 Image image = null;
		    GC gc = null;
		    Graphics g = null;
		    
		    try {
		        image = new Image(device, r.width, r.height);
		        gc = new GC(image);
		        g = new SWTGraphics(gc);
		        g.translate(r.x * -1, r.y * -1);

		        figure.paint(g);

		        ImageLoader imageLoader = new ImageLoader();
		        imageLoader.data = new ImageData[] {image.getImageData()};
		        imageLoader.save(fileName, format);
		        
		    } catch (Exception e){
		    	e.printStackTrace();
		    } finally {
		        if (g != null) {
		        	g.dispose();
		        }
		        if (gc != null) {
		            gc.dispose();
		        }
		        if (image != null) {
		            image.dispose();
		        }
		    }
	}
	
	public void createSnapshot(IFigure figure,
			String fileName, XRayWorkbenchView viewContainer,
			SnapShotAction snapShotAction){
		if (fileName != null) {
			this.viewContainer = viewContainer;
			this.snapShotAction = snapShotAction;
			
			createImage(figure, SWT.IMAGE_PNG, fileName);
		}
	}

}
