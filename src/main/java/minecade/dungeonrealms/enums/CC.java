package minecade.dungeonrealms.enums;

import org.fusesource.jansi.Ansi;

public enum CC {
	
	BLACK(Ansi.ansi().fg(Ansi.Color.BLACK).boldOff()),
	BLUE(Ansi.ansi().fg(Ansi.Color.BLUE).boldOff()),
	CYAN(Ansi.ansi().fg(Ansi.Color.CYAN).boldOff()),
	DEFAULT(Ansi.ansi().fg(Ansi.Color.DEFAULT).boldOff()),
	GREEN(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff()),
	MAGENTA(Ansi.ansi().fg(Ansi.Color.MAGENTA).boldOff()),
	RED(Ansi.ansi().fg(Ansi.Color.RED).boldOff()),
	WHITE(Ansi.ansi().fg(Ansi.Color.WHITE).boldOff()),
	YELLOW(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff());
	
	private Ansi color;
	
	private CC(Ansi color) {
		this.color = color;
	}
	
	@Override
	public String toString() {
		return color.toString();
	}
	
}
