package jade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;

public class JSONMessageLogger extends MessageLogger {
	//static Logger _LOG = Logger.getLogger(MainRunner.class);
	int currNestingLevel;
	public JSONMessageLogger(String pref) {
		super(pref);
		currNestingLevel = -1;
	}
	public JSONMessageLogger(String pref, String separator) {
		super(pref, separator);
		currNestingLevel = -1;
	}

	// no-op for streaming to std out. close objects for JSON
	public void close() {
		while (this.currNestingLevel > -1) {
			for (int i = 0; i < this.currNestingLevel + 1; ++i) {
				this.outStream.print("  ");
			}
			this.outStream.println("]");
			this.currNestingLevel -= 1;
		}
		currNestingLevel = -1;
	}

	// prepend prefix (or no-op in JSON)
	protected void _write_prefix() {
	}

	// indent with spaces (or nest if JSON)
	protected void _indent(int indentLevel) {
		if (indentLevel == this.currNestingLevel) {
			for (int i = 0; i < this.currNestingLevel + 1; ++i) {
				this.outStream.print("  ");
			}
			this.outStream.print(", {");
		} else if (indentLevel > this.currNestingLevel) {
			while (this.currNestingLevel < indentLevel) {
				for (int i = 0; i < this.currNestingLevel + 1; ++i) {
					this.outStream.print("  ");
				}
				if (this.currNestingLevel > -1) {
					this.outStream.print(',');
				}
				this.outStream.println("[");
				this.currNestingLevel += 1;
			}
			for (int i = 0; i < this.currNestingLevel + 1; ++i) {
				this.outStream.print("  ");
			}
			this.outStream.print("{");
		} else {
			while (this.currNestingLevel > indentLevel) {
				for (int i = 0; i < this.currNestingLevel + 1; ++i) {
					this.outStream.print("  ");
				}
				this.outStream.println("]");
				this.currNestingLevel -= 1;
			}
			for (int i = 0; i < this.currNestingLevel + 1; ++i) {
				this.outStream.print("  ");
			}
			this.outStream.print(", {");
		}
	}

	public static String escapeStr(String s) {
		return '"' + StringUtils.join(s.split("\""), "\\\"") + '"';
	}

	protected void _message(String label) {
		this.outStream.println("\"label\":" + escapeStr(label) + '}');
	}

	protected void _messageStrStr(String label, String s, String s2) {
		this.outStream.println("\"label\": " + escapeStr(label) + ", \"" + s + "\": " + escapeStr(s2) + "}");
	}
	protected void _messageStrLong(String label, String s, Long i) {
		this.outStream.println("\"label\": " + escapeStr(label) + ", \"" + s + "\": " + i + "}");
	}

	protected void _messageStrInt(String label, String s, int i) {
		this.outStream.println("\"label\": " + escapeStr(label) + ", \"" + s + "\": " + i + "}");
	}

	protected void _messageStrStrStrStr(String label, String s, String s2, String s3, String s4) {
		this.outStream.println("\"label\": " + escapeStr(label) + ", \"" + s + "\": " + escapeStr(s2) + ", \"" + s3 + "\": " + escapeStr(s4) + "}");
	}
	protected void _messageStrLongStrStr(String label, String s, Long i, String s3, String s4) {
		this.outStream.println("\"label\": " + escapeStr(label) + ", \"" + s + "\": " + i + ", \"" + s3 + "\": " + escapeStr(s4) + "}");
	}

	protected void _messageStrIntStrStr(String label, String s, int i, String s3, String s4) {
		this.outStream.println("\"label\": " + escapeStr(label) + ", \"" + s + "\": " + i + ", \"" + s3 + "\": " + escapeStr(s4) + "}");
	}

}
