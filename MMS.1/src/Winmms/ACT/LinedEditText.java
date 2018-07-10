package Winmms.ACT;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * A custom EditText that draws lines between each line of text that is displayed.
 * 这是一个自定义的EditView被画很多行再每两行的中间
*/
 public class LinedEditText extends EditText 
 {
 /*
 * 一个是矩形 一个是装绘 主要显示画图的样式
 */
	 private Rect mRect;
	 private Paint mPaint;
 
 // we need this constructor for LayoutInflater
 /*
 * 构造函数
 */
	 public LinedEditText(Context context, AttributeSet attrs) 
	 {
		 super(context, attrs);
		 
		 mRect = new Rect();
		 mPaint = new Paint();
		 mPaint.setStyle(Paint.Style.STROKE);
		 mPaint.setColor(0xFFBDCCCC);
	 }
 /**
  *  这个是具体的画法
  * (non-Javadoc)
  * @see android.widget.TextView#onDraw(android.graphics.Canvas)
  */
	 protected void onDraw(Canvas canvas) 
	 {
		 int count = getLineCount();
		 Rect r = mRect;
		 Paint paint = mPaint;
 
		 for (int i = 0; i < count; i++) 
		 {
			 int baseline = getLineBounds(i, r);
			 canvas.drawLine(r.left - 2, baseline + 12, r.right + 2, baseline + 12, paint);
		 }
		 super.onDraw(canvas);
	 }
 }
