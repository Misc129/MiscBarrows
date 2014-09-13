package MiscBarrows;

import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Time;

public class AttackStyles {
	
	private static int SPELLBOOK_WIDGET = 192;
	private static int COMBAT_SORT_BUTTON = 19;
	
	enum StandardSpells{
		MAGIC_DART(56),
		WIND_SURGE(84),
		WATER_SURGE(87),
		EARTH_SURGE(89),
		FIRE_SURGE(91);
		
		private int _widgetchild;
		
		private StandardSpells(int widgetchild){
			_widgetchild = widgetchild;
		}
		
		public static boolean isAutoCasting(){
			return Settings.get(43,4) == 4;
		}
		
		public void setCast(){
			Tabs.MAGIC.open();
			Time.sleep(700);
			Widgets.get(SPELLBOOK_WIDGET,_widgetchild).click(true);
			Time.sleep(700);
			Tabs.INVENTORY.open();
			Time.sleep(700);
		}
		
		public static void sortByCombat(){
			Tabs.MAGIC.open();
			Time.sleep(700);
			if(!Widgets.get(SPELLBOOK_WIDGET).validate()){
				sortByCombat();
				return;
			}
			Mouse.click(Widgets.get(SPELLBOOK_WIDGET, COMBAT_SORT_BUTTON).getCentralPoint(),true);
			Time.sleep(700);
			Tabs.INVENTORY.open();
			Time.sleep(700);
		}
	}
	enum AncientsSpells{
		KHRYLL_TELEPORT(42);
		
		private static int SPELLBOOK_WIDGET = 193;
		private static int COMBAT_SORT_BUTTON = 10;
		
		private int _widgetchild;
		
		private AncientsSpells(int widgetchild){
			_widgetchild = widgetchild;
		}
		
		public static boolean isAutoCasting(){
			return Settings.get(43,4) == 4;
		}
		
		public void setCast(){
			Tabs.MAGIC.open();
			Time.sleep(700);
			Widgets.get(SPELLBOOK_WIDGET,_widgetchild).click(true);
			Time.sleep(700);
			Tabs.INVENTORY.open();
			Time.sleep(700);
		}
		
		public static void sortByCombat(){
			Tabs.MAGIC.open();
			Time.sleep(700);
			if(!Widgets.get(SPELLBOOK_WIDGET).validate()){
				sortByCombat();
				return;
			}
			Mouse.click(Widgets.get(SPELLBOOK_WIDGET, COMBAT_SORT_BUTTON).getCentralPoint(),true);
			Time.sleep(700);
			Tabs.INVENTORY.open();
			Time.sleep(700);
		}
	}

	public enum Rune{
		AIR(556),
		WATER(555),
		EARTH(557),
		FIRE(554),
		MIND(558),
		CHAOS(562),
		DEATH(560),
		BLOOD(565),
		LAW(563);
		
		private int _id;
		
		private Rune(int id){
			_id = id;
		}
		
		public int getId(){
			return _id;
		}
	}
}

