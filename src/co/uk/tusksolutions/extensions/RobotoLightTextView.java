package co.uk.tusksolutions.extensions;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoLightTextView extends TextView {

	static String FONT_NAME = "fonts/Roboto/Roboto-Light.ttf";

	public RobotoLightTextView(Context context) {
		super(context);

		Typeface face = Typeface
				.createFromAsset(context.getAssets(), FONT_NAME);
		this.setTypeface(face);
	}

	public RobotoLightTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface face = Typeface
				.createFromAsset(context.getAssets(), FONT_NAME);
		this.setTypeface(face);
	}

	public RobotoLightTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface face = Typeface
				.createFromAsset(context.getAssets(), FONT_NAME);
		this.setTypeface(face);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
