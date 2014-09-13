<<<<<<< HEAD

=======
package MiscBarrows;
>>>>>>> Upload src

import java.awt.Point;

import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Time;

public class Prayer {

	static final int PRAYERS_WIDGET = 271;
	static final int PRAYER_ICONS_WIDGET = 8;
	
<<<<<<< HEAD
	public enum Protect{
		MAGIC(Type.PROTECT_MAGIC, Type.DEFLECT_MAGIC),
		RANGED(Type.PROTECT_RANGED, Type.DEFLECT_RANGED),
		MELEE(Type.PROTECT_MELEE, Type.DEFLECT_MELEE);
		
		public Type regular;
		public Type curses;
		
		Protect(Type regular, Type curses){
			this.regular = regular;
			this.curses = curses;
		}
		
		public void enable(boolean tabBack){
			Type t = isCursesOn() ? curses : regular;
			if (getPrayerPoints() == 0) return;
			if(getPrayerIcon() == t.getIcon()) return;
			Tabs.PRAYER.open();
			Time.sleep(500);
			if (!Widgets.get(PRAYERS_WIDGET).validate())
				return;
			Widgets.get(PRAYERS_WIDGET).getChild(PRAYER_ICONS_WIDGET)
			.getChild(t.getWidgetchild()).click(true);
			Time.sleep(500);
			if(tabBack)
				Tabs.INVENTORY.open();
		}
		
		public boolean enableFast(){
			Type t = isCursesOn() ? curses : regular;
			if(Widgets.get(PRAYERS_WIDGET).validate()){
				Point p = Widgets.get(PRAYERS_WIDGET).getChild(PRAYER_ICONS_WIDGET)
						.getChild(t.getWidgetchild()).getCentralPoint();
				return Mouse.click(p.x,p.y,true);
			}
			return false;
		}
		
		public static void disable(boolean tabBack){
			Type t = Type.getOverhead();
			if(t == null) return;
			Tabs.PRAYER.open();
			Time.sleep(500);
			if(Widgets.get(PRAYERS_WIDGET).validate()){
				Widgets.get(PRAYERS_WIDGET).getChild(PRAYER_ICONS_WIDGET).
						getChild(t.getWidgetchild()).click(true);
			}
			if(tabBack)
				Tabs.INVENTORY.open();
		}
		
		public boolean isProtecting(){
			Type t = Type.getOverhead();
			return (t == regular || t == curses);
		}
		
		public static Protect getProtect(){
			for(Protect p : Protect.values()){
				if(p.isProtecting())
					return p;
			}
			return null;
		}
	}
	public enum Type{
		/*normal*/
		PROTECT_MAGIC(2,17),
		PROTECT_RANGED(1,18),
		PROTECT_MELEE(0,19),
		
		/*curses*/
		DEFLECT_MAGIC(13,7),
		DEFLECT_RANGED(14,8),
		DEFLECT_MELEE(12,9);
		
=======
	public enum Type{
		PROTECT_MAGIC(2,17),
		PROTECT_RANGED(1,18),
		PROTECT_MELEE(0,19),
		DEFLECT_MAGIC(13,7),
		DEFLECT_RANGED(14,8),
		DEFLECT_MELEE(12,9);

>>>>>>> Upload src
		private int _icon;
		private int _widgetChild;

		private Type(int icon, int widgetChild){
			_icon = icon;
			_widgetChild = widgetChild;
		}

		public boolean isEnabled(){
			return getPrayerIcon() == _icon;
		}

		public void enable(boolean tabBack){
			if (getPrayerPoints() == 0) return;
			if(getPrayerIcon() == _icon) return;
			Tabs.PRAYER.open();
			Time.sleep(500);
			if (!Widgets.get(PRAYERS_WIDGET).validate())
				return;
			Widgets.get(PRAYERS_WIDGET).getChild(PRAYER_ICONS_WIDGET)
			.getChild(_widgetChild).click(true);
			Time.sleep(500);
			if(tabBack)
				Tabs.INVENTORY.open();
		}

		public boolean enableFast(){
			if(Widgets.get(PRAYERS_WIDGET).validate()){
				Point p = Widgets.get(PRAYERS_WIDGET).getChild(PRAYER_ICONS_WIDGET)
						.getChild(_widgetChild).getCentralPoint();
<<<<<<< HEAD
				return Mouse.click(p.x,p.y,true);
			}
			return false;
		}
		
=======
				Mouse.click(p.x,p.y,true);
			}
			return false;
		}

>>>>>>> Upload src
		public static void disable(){
			int currentIcon = getPrayerIcon();
			if(currentIcon == -1) return;
			int widgetchild = -1;
			for(Type n : Type.values()){
				if(currentIcon == n.getIcon())
					widgetchild = n.getWidgetchild();
			}
			if(widgetchild == -1) return;
			if(!Tabs.getCurrent().equals(Tabs.PRAYER))
				Tabs.PRAYER.open();
			Time.sleep(500);
			if (!Widgets.get(PRAYERS_WIDGET).validate())
				return;
			Widgets.get(PRAYERS_WIDGET).getChild(PRAYER_ICONS_WIDGET)
					.getChild(widgetchild).click(true);
			Time.sleep(500);
			Tabs.INVENTORY.open();
		}
<<<<<<< HEAD
=======
		
>>>>>>> Upload src

		public static Type getOverhead() {
			int icon = getPrayerIcon();
			Type result = null;
			for(Type n : Type.values()){
				if(n.getIcon() == icon)
					result = n;
			}
			return result;
		}
		
		public int getIcon(){
			return _icon;
		}
		
		public int getWidgetchild(){
			return _widgetChild;
		}
	}
	
	public static boolean isCursesOn(){
		return Settings.get(1584) % 2 != 0;
	}
	
	public static int getPrayerPoints() {
		return Integer.parseInt(Widgets.get(749).getChild(6).getText());
	}
	

	public static int getPrayerIcon() {
		return Players.getLocal().getPrayerIcon();
	}
	
	public static int getLevel(){
		return Skills.getRealLevel(Skills.PRAYER);
	}
	
}