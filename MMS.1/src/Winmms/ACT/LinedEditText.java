package Winmms.ACT;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * A custom EditText that draws lines between each line of text that is displayed.
 * ����һ���Զ����EditView�����ܶ�����ÿ���е��м�
*/
 public class LinedEditText extends EditText 
 {
 /*
 * һ���Ǿ��� һ����װ�� ��Ҫ��ʾ��ͼ����ʽ
 */
	 private Rect mRect;
	 private Paint mPaint;
 
 // we need this constructor for LayoutInflater
 /*
 * ���캯��
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
  *  ����Ǿ���Ļ���
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
