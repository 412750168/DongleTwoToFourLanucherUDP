package net.bestidear.donglelauncher;

import net.bestidear.donglelauncher.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class InstructionsActivity extends Activity {

	//private static final String INSTRUCTIONNAME = "Instructions.txt";
	private static final int NEXT = 0x500 + 1;
	private int count = 0;

	private TextView textview_instructions;
	private int str[] = new int[] { R.string.instr_introduction,
			R.string.instr_function, R.string.instr_mode,
			R.string.instr_approach, R.string.instr_fragement };

	private Handler handler = null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.instructions);
		textview_instructions = (TextView) findViewById(R.id.textview_instructions);
		// String str = getFromAssets(INSTRUCTIONNAME);
		// textview_instructions.setText(str);
		textview_instructions.setText(str[0]);

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				count++;
				if (count == 5)
					count = 0;

				switch (msg.what) {
				case NEXT:
					textview_instructions.setText(str[count]);
					break;
				}

			}

		};
	}

	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		// TODO Auto-generated method stub
		handler.obtainMessage(NEXT).sendToTarget();
		return super.onKeyDown(arg0, arg1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			handler.obtainMessage(NEXT).sendToTarget();
		return super.onTouchEvent(event);
	}

	/*
	 * private String getFromAssets(String fileName) { try { InputStreamReader
	 * inputReader = new InputStreamReader(
	 * getResources().getAssets().open(fileName)); BufferedReader bufReader =
	 * new BufferedReader(inputReader); String line = ""; String Result = "";
	 * while ((line = bufReader.readLine()) != null) Result += "\n" + line;
	 * return Result; } catch (Exception e) { e.printStackTrace(); } return
	 * "No data!!"; }
	 */
}
