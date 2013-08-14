package me.dhanoop.dcrypt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class DCryptPictureViewer extends Activity implements OnTouchListener {

	private static final String TAG = "DCrypt - Touch";
	
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Matrix originalMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    
    
	String currentPicId, prevPicId, nextPicId;
	int picPosition;
	private ImageView dCryptImageView = null;
	private Button prevButton = null;
	private Button nextButton = null;
	private Button returnButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picviewer);
		
		dCryptImageView = (ImageView) findViewById(R.id.dcryptImageView);
		prevButton = (Button) findViewById(R.id.prevButton);
		nextButton = (Button) findViewById(R.id.nextButton);
		returnButton = (Button) findViewById(R.id.returnButton);
		
		Intent i = getIntent();

	    currentPicId = i.getStringExtra("currentPicId");
	    prevPicId = i.getStringExtra("prevPicId");
	    nextPicId = i.getStringExtra("nextPicId");
	    picPosition = i.getIntExtra("picPosition", -1);
	    
	    currentPicId = (DCryptImageAdapter.getCurrItemId(picPosition)).getAbsolutePath();

	    Log.i("DCrypt", "currentPicId" + currentPicId);

	    Drawable image;
	    if (currentPicId == null) {
	    	image = getResources().getDrawable(R.drawable.ic_launcher);
	    } else {
	    	image = Drawable.createFromPath(currentPicId);
	    }
	    
	    dCryptImageView.setImageDrawable(image);
	    dCryptImageView.setOnTouchListener(this);
	    dCryptImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	    scaleProperly(image.getMinimumHeight(), image.getMinimumWidth());
	    //originalMatrix = dCryptImageView.getImageMatrix();
	    //dCryptImageView.setScaleType(ImageView.ScaleType.MATRIX);
	    //dCryptImageView.setImageMatrix(originalMatrix);	    
	    
	    returnButton.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View view){
	            finish();   
	        }
	    });
	    
	    prevButton.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View view){
	        	Drawable imagePrev;	        	
	        	currentPicId = prevPicId;
	    	    if (prevPicId == null) {
	    	    	imagePrev = getResources().getDrawable(R.drawable.ic_launcher);
	    	    } else {
	    	    	imagePrev = Drawable.createFromPath(currentPicId);
	    	    }	   
	    	    
	            dCryptImageView.setImageDrawable(imagePrev);
	            dCryptImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	            //dCryptImageView.setImageMatrix(originalMatrix);
	            
	            picPosition--;
	            prevPicId = (DCryptImageAdapter.getPrevItemId(picPosition)).getAbsolutePath();
	            nextPicId = (DCryptImageAdapter.getNextItemId(picPosition)).getAbsolutePath();	            
	        }
	    });
	    
	    nextButton.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View view){
	        	Drawable imageNext;	        	
	        	currentPicId = nextPicId;
	    	    if (currentPicId == null) {
	    	    	imageNext = getResources().getDrawable(R.drawable.ic_launcher);
	    	    } else {
	    	    	imageNext = Drawable.createFromPath(currentPicId);
	    	    }
	             
	            dCryptImageView.setImageDrawable(imageNext);
	            dCryptImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	            //dCryptImageView.setImageMatrix(originalMatrix);
	            
	            picPosition++;
	            nextPicId = (DCryptImageAdapter.getNextItemId(picPosition)).getAbsolutePath();
	            prevPicId = (DCryptImageAdapter.getPrevItemId(picPosition)).getAbsolutePath();	            
	        }
	    });
	}
	
	/**
	 * 
	 */
    @SuppressWarnings("deprecation")
	private void scaleProperly(int height, int width) {						
		int measuredWidth = 0;
		int measuredHeight = 0;
		Point size = new Point();
		WindowManager w = getWindowManager();

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
			w.getDefaultDisplay().getSize(size);			
			measuredWidth = size.x;
			measuredHeight = size.y; 
        } else {
        	Display d = w.getDefaultDisplay(); 
        	measuredWidth = d.getWidth(); 
        	measuredHeight = d.getHeight(); 
        }
		
		float scale = measuredWidth / width;
		Log.i("DCrypt", "Height=" + measuredHeight + "; Width=" + measuredWidth);
		//originalMatrix = new Matrix();
		//originalMatrix.postScale(0.25f, 0.25f);
		matrix.postScale(0.20f, 0.20f);
	}


	/**
     * 
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * 
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        toLogger(event);
        
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				Log.d(TAG, "mode=DRAG"); // write to LogCat
				mode = DRAG;
				break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) { 
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); 
                    // create the transformation in the matrix  of points
                } else if (mode == ZOOM) { 
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                                                    // matrix...if scale > 1 means
                                                    // zoom in...if scale < 1 means
                                                    // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
	}

	/**
	 * 
	 * @param event
	 */
	@SuppressWarnings("deprecation")
	private void toLogger(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || 
        	actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount()) {
			    sb.append(";");
			}
        }
        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
	}
	
}
