package MiscBarrows;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Magic;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.Character;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.event.MessageEvent;
import org.powerbot.game.bot.event.listener.MessageListener;
import org.powerbot.game.bot.event.listener.PaintListener;



@Manifest(name = "RevertBarrows",
description = "v0.77 Does barrows. Customizable GUI. Start at canifis.",
version = 0.77,
authors = { "Misc" })
public class MiscBarrows extends ActiveScript implements PaintListener,
		MessageListener, MouseListener {

	/*
	 * TODO 
	 * IMPORTANT:
	 * 	BUGS:
	 *  if all 6 brothers have been killed and you are just starting script, it wont find the cryptHole FIX!, rare situation, have to just
	 *      make a strategy that goes to each tomb and searches....find settings
	 *  change if plane == 3 to if(detect tomb) because akrisae crypt is on plane 0

	 * 
	 * 
	 * to implement:
	 *  autocast spell (only thing left should be to add the amt/type of runes to prepinv, in the GUI)
	 *  akrisae
	 *    
	 * 
	 * 
	 * 
	 * small fix: 
	 * stop clicking walk during boat ride.
	 */
	/*
	 * NOTES 
	 * crypthole settings?
	 * 
	 * VERAC: 1189 - 523(0X20b) -> 271(0x10f)
	 * KARIL: 1189 - 523(0x20b) -> 270(0x10e)
	 * AHRIM : 1189 - 523(0x20b) -> 197(0xc5)
	 * 
	 */

	final int[] PRAYER_POTIONS = { 143, 141, 139, 2434 };
	final int[] BARROWS_LOOT = { 565, 560, 4740, 558, 562, 1149, 987, 985,
			4708, 4712, 4714, 4710, 4716, 4720, 4722, 4718, 4745, 4749, 4751,
			4747, 4753, 4757, 4759, 4755, 4724, 4728, 4730, 4726, 4732, 4736,
			4738, 4734 };

	final int PRAYER_POTION_4_ID = 2434;
	final int SUPER_DEFENCE_POTION_2 = 165;
	final int STRENGTH_POTION_2_ID = 117;
	final int PRAYER_POTION_2_ID = 141;
	final int RESTORE_POTION_2_ID = 129;
	final int SHARK_ID = 385;
	final int VIAL_ID = 229;
	final int ROTTON_FOOD_ID = 2959;
	static final int POLYPORE_STICK = 22498;
	final int[] JUNK_IDS = { 165, 117, 129 };
	
	final Area BANK_AREA = new Area(new Tile(3507,3484,0),new Tile(3517,3473,0));

	static final Tile BANK_TILE = new Tile(3511,3480,0);

	final int KILLED_LIST_WIDGET = 24;

	final int MAGIC_PRAYER = 0;
	final int RANGED_PRAYER = 1;
	final int MELEE_PRAYER = 2;
	
	final int MAGIC_PRAYER_ICON = 2;
	final int RANGED_PRAYER_ICON = 1;
	final int MELEE_PRAYER_ICON = 0;
	final int MAGIC_DEFLECT_ICON = 13;
	final int RANGED_DEFLECT_ICON = 14;
	final int MELEE_DEFLECT_ICON = 12;

	final int PRAYERS_WIDGET = 271;
	final int CURSE_ICONS_WIDGET = 7;
	final int PRAYER_ICONS_WIDGET = 8;
	final int MAGIC_PRAYER_WIDGET_CHILD = 17;
	final int RANGED_PRAYER_WIDGET_CHILD = 18;
	final int MELEE_PRAYER_WIDGET_CHILD = 19;
	
	final int MAGIC_DEFLECT_WIDGET_CHILD = 7;
	final int RANGED_DEFLECT_WIDGET_CHILD = 8;
	final int MELEE_DEFLECT_WIDGET_CHILD = 9;

	final int QUICK_PRAYER_WIDGET = 749;
	final int QUICK_PRAYER_WIDGET_CHILD = 2;

	final int PUZZLE_WIDGET = 25;
	final int[] PUZZLE_WIDGET_CHILD_CHOICES = { 2, 3, 5 };
	final int[] PUZZLE_MODEL_ANSWERS = { 6731, 6713, 6719, 6725 };

	final int CHAT_MENU_WIDGET = 1186;
	final int CHAT_MENU_FORWARD_WIDGET_CHILD = 7;
	final int CHAT_OPTION_MENU_WIDGETS = 1188;
	final int CHAT_OPTION_MENU_OK_WIDGET_CHILD = 2;

	static final int NW_ROPE_ID = 6709;
	static final int NE_ROPE_ID = 6710;
	static final int SW_ROPE_ID = 6711;
	static final int SE_ROPE_ID = 6712;

	static final int CRYPT_CHEST_ID = 10284;

	CryptDoor NW_N_DOOR = new CryptDoor("NW_N_DOOR", 6735, new Tile(3535, 9718,0), 10);
	CryptDoor NW_W_DOOR = new CryptDoor("NW_W_DOOR", 6736, new Tile(3528, 9712,0), 11);
	CryptDoor NW_S_DOOR = new CryptDoor("NW_S_DOOR", 6737, new Tile(3534, 9705,0), 12);
	CryptDoor NW_E_DOOR = new CryptDoor("NW_E_DOOR", 6738, new Tile(3541, 9711,0), 13);
	CryptDoor N_W_DOOR = new CryptDoor("N_W_DOOR", 6738,new Tile(3545, 9712, 0), 13);
	CryptDoor N_S_DOOR = new CryptDoor("N_S_DOOR", 6739,new Tile(3551, 9705, 0), 14);
	CryptDoor N_E_DOOR = new CryptDoor("N_E_DOOR", 6740,new Tile(3558, 9711, 0), 15);
	CryptDoor NE_N_DOOR = new CryptDoor("NE_N_DOOR", 6735, new Tile(3569, 9718,0), 10);
	CryptDoor NE_W_DOOR = new CryptDoor("NE_W_DOOR", 6740, new Tile(3562, 9712,0), 15);
	CryptDoor NE_S_DOOR = new CryptDoor("NE_S_DOOR", 6741, new Tile(3568, 9705,0), 16);
	CryptDoor NE_E_DOOR = new CryptDoor("NE_E_DOOR", 6742, new Tile(3575, 9711,0), 17);

	CryptDoor W_N_DOOR = new CryptDoor("W_N_DOOR", 6737,new Tile(3535, 9701, 0), 12);
	CryptDoor W_S_DOOR = new CryptDoor("W_S_DOOR", 6745,new Tile(3534, 9688, 0), 20);
	CryptDoor W_E_DOOR = new CryptDoor("W_E_DOOR", 6743,new Tile(3541, 9694, 0), 18);

	CryptDoor C_N_DOOR = new CryptDoor("C_N_DOOR", 6739,new Tile(3552, 9701, 0), 14);
	CryptDoor C_W_DOOR = new CryptDoor("C_W_DOOR", 6743,new Tile(3545, 9695, 0), 18);
	CryptDoor C_S_DOOR = new CryptDoor("C_S_DOOR", 6746,new Tile(3551, 9688, 0), 21);
	CryptDoor C_E_DOOR = new CryptDoor("C_E_DOOR", 6744,new Tile(3558, 9694, 0), 19);

	CryptDoor E_N_DOOR = new CryptDoor("E_N_DOOR", 6741,new Tile(3569, 9701, 0), 16);
	CryptDoor E_W_DOOR = new CryptDoor("E_W_DOOR", 6744,new Tile(3562, 9695, 0), 19);
	CryptDoor E_S_DOOR = new CryptDoor("E_S_DOOR", 6747,new Tile(3568, 9688, 0), 22);

	CryptDoor SW_N_DOOR = new CryptDoor("SW_N_DOOR", 6745, new Tile(3535, 9684,0), 20);
	CryptDoor SW_W_DOOR = new CryptDoor("SW_W_DOOR", 6736, new Tile(3528, 9678,0), 11);
	CryptDoor SW_S_DOOR = new CryptDoor("SW_S_DOOR", 6750, new Tile(3534, 9671,0), 25);
	CryptDoor SW_E_DOOR = new CryptDoor("SW_E_DOOR", 6748, new Tile(3541, 9677,0), 23);

	CryptDoor S_N_DOOR = new CryptDoor("S_N_DOOR", 6746,new Tile(3552, 9684, 0), 21);
	CryptDoor S_W_DOOR = new CryptDoor("S_W_DOOR", 6748,new Tile(3545, 9678, 0), 23);
	CryptDoor S_E_DOOR = new CryptDoor("S_E_DOOR", 6749,new Tile(3558, 9677, 0), 24);

	CryptDoor SE_N_DOOR = new CryptDoor("SE_N_DOOR", 6747, new Tile(3569, 9684,0), 22);
	CryptDoor SE_W_DOOR = new CryptDoor("SE_W_DOOR", 6749, new Tile(3562, 9678,0), 24);
	CryptDoor SE_S_DOOR = new CryptDoor("SE_S_DOOR", 6750, new Tile(3568, 9671,0), 25);
	CryptDoor SE_E_DOOR = new CryptDoor("SE_E_DOOR", 6742, new Tile(3575, 9677,0), 17);
	CryptDoor[] doors = { SE_N_DOOR, SE_W_DOOR, SE_S_DOOR, SE_E_DOOR, S_N_DOOR,
			S_W_DOOR, S_E_DOOR, SW_N_DOOR, SW_W_DOOR, SW_S_DOOR, SW_E_DOOR,
			E_N_DOOR, E_W_DOOR, E_S_DOOR, C_N_DOOR, C_W_DOOR, C_S_DOOR,
			C_E_DOOR, W_N_DOOR, W_S_DOOR, W_E_DOOR, NE_N_DOOR, NE_W_DOOR,
			NE_S_DOOR, NE_E_DOOR, N_W_DOOR, N_S_DOOR, N_E_DOOR, NW_N_DOOR,
			NW_W_DOOR, NW_S_DOOR, NW_E_DOOR };
	CryptDoor[] centerDoors = { N_S_DOOR, W_E_DOOR, S_N_DOOR, E_W_DOOR };

	enum PaintPoint {
		NW(327, 373), N(361, 373), NE(395, 373), W(327, 404), C(361, 404), E(
				395, 404), SW(327, 434), S(361, 434), SE(395, 434);
		PaintPoint(int x, int y) {
			_point = new Point(x, y);
		}

		private Point _point;

		Point getPoint() {
			return _point;
		}
	}

	final CryptDoor[] NW_ROOM_DOORS = { NW_E_DOOR, NW_S_DOOR, NW_N_DOOR,NW_W_DOOR };
	final CryptRoom NW_ROOM = new CryptRoom("NW_ROOM", new Area(new Tile(3529,9718, 0), new Tile(3541, 9706, 0)), NW_ROOM_DOORS);
	final CryptDoor[] N_ROOM_DOORS = { N_S_DOOR, N_E_DOOR, N_W_DOOR };
	final CryptRoom N_ROOM = new CryptRoom("N_ROOM", new Area(new Tile(3546,9718, 0), new Tile(3558, 9706, 0)), N_ROOM_DOORS);
	final CryptDoor[] NE_ROOM_DOORS = { NE_S_DOOR, NE_W_DOOR, NE_N_DOOR,NE_E_DOOR };
	final CryptRoom NE_ROOM = new CryptRoom("NE_ROOM", new Area(new Tile(3563,9718, 0), new Tile(3575, 9706, 0)), NE_ROOM_DOORS);
	final CryptDoor[] W_ROOM_DOORS = { W_E_DOOR, W_N_DOOR, W_S_DOOR };
	final CryptRoom W_ROOM = new CryptRoom("W_ROOM", new Area(new Tile(3529,9701, 0), new Tile(3541, 9689, 0)), W_ROOM_DOORS);
	final CryptDoor[] C_ROOM_DOORS = { C_N_DOOR, C_W_DOOR, C_S_DOOR, C_E_DOOR };
	final CryptRoom C_ROOM = new CryptRoom("C_ROOM", new Area(new Tile(3546,9701, 0), new Tile(3558, 9689, 0)), C_ROOM_DOORS);
	final CryptDoor[] E_ROOM_DOORS = { E_W_DOOR, E_S_DOOR, E_N_DOOR };
	final CryptRoom E_ROOM = new CryptRoom("E_ROOM", new Area(new Tile(3563,9701, 0), new Tile(3575, 9689, 0)), E_ROOM_DOORS);
	final CryptDoor[] SW_ROOM_DOORS = { SW_N_DOOR, SW_E_DOOR, SW_W_DOOR,SW_S_DOOR };
	final CryptRoom SW_ROOM = new CryptRoom("SW_ROOM", new Area(new Tile(3529,9684, 0), new Tile(3541, 9672, 0)), SW_ROOM_DOORS);
	final CryptDoor[] S_ROOM_DOORS = { S_N_DOOR, S_W_DOOR, S_E_DOOR };
	final CryptRoom S_ROOM = new CryptRoom("S_ROOM", new Area(new Tile(3546,9684, 0), new Tile(3558, 9672, 0)), S_ROOM_DOORS);
	final CryptDoor[] SE_ROOM_DOORS = { SE_W_DOOR, SE_N_DOOR, SE_S_DOOR,SE_E_DOOR };
	final CryptRoom SE_ROOM = new CryptRoom("SE_ROOM", new Area(new Tile(3563,9684, 0), new Tile(3574, 9672, 0)), SE_ROOM_DOORS);
	CryptRoom[] rooms = { NW_ROOM, N_ROOM, NE_ROOM, W_ROOM, C_ROOM, E_ROOM,
			SW_ROOM, S_ROOM, SE_ROOM };

	// each side path in the counter-clockwise direction
	final Tile[] NORTH_TUNNEL_PATH = { new Tile(3568, 9720, 0),
			new Tile(3566, 9721, 0), new Tile(3563, 9721, 0),
			new Tile(3560, 9721, 0), new Tile(3557, 9722, 0),
			new Tile(3554, 9721, 0), new Tile(3551, 9722, 0),
			new Tile(3548, 9721, 0), new Tile(3545, 9722, 0),
			new Tile(3542, 9722, 0), new Tile(3539, 9721, 0),
			new Tile(3536, 9721, 0), new Tile(3535, 9718, 0) };
	final Tile[] WEST_TUNNEL_PATH = { new Tile(3527, 9711, 0),
			new Tile(3525, 9709, 0), new Tile(3524, 9706, 0),
			new Tile(3524, 9703, 0), new Tile(3524, 9700, 0),
			new Tile(3525, 9697, 0), new Tile(3524, 9694, 0),
			new Tile(3525, 9691, 0), new Tile(3525, 9688, 0),
			new Tile(3525, 9685, 0), new Tile(3525, 9682, 0),
			new Tile(3526, 9679, 0), new Tile(3528, 9678, 0) };
	final Tile[] SOUTH_TUNNEL_PATH = { new Tile(3535, 9669, 0),
			new Tile(3538, 9668, 0), new Tile(3541, 9668, 0),
			new Tile(3544, 9667, 0), new Tile(3547, 9668, 0),
			new Tile(3550, 9668, 0), new Tile(3553, 9667, 0),
			new Tile(3556, 9667, 0), new Tile(3559, 9667, 0),
			new Tile(3562, 9668, 0), new Tile(3565, 9668, 0),
			new Tile(3568, 9669, 0) };
	final Tile[] EAST_TUNNEL_PATH = { new Tile(3577, 9678, 0),
			new Tile(3578, 9681, 0), new Tile(3579, 9684, 0),
			new Tile(3579, 9687, 0), new Tile(3579, 9690, 0),
			new Tile(3579, 9693, 0), new Tile(3578, 9696, 0),
			new Tile(3578, 9699, 0), new Tile(3578, 9702, 0),
			new Tile(3579, 9705, 0), new Tile(3579, 9708, 0),
			new Tile(3577, 9711, 0) };


	
	/*
	 * DHAROK: 63177
	 * VERAC: 66016
	 * TORAG: 66019
	 * KARIL: 66018
	 * GUTHAN: 66020
	 * AHRIM: 66017
	 */


	enum CryptMonster {
		BLOODWORM(2031, 2070), RAT(2032, 240), GIANT_RAT(4921, 14859), CRYPT_SPIDER(
				2034, 6249), GIANT_CRYPT_SPIDER(2035, 5327), SKELETON(2036,
				12632), ARMORED_SKELETON(2037, 5485);
		CryptMonster(int id, int attackAnimation) {
			_id = id;
			_attackAnimation = attackAnimation;
		}

		int _id;
		int _attackAnimation;

		int getId() {
			return _id;
		}

		int getAttackAnimation() {
			return _attackAnimation;
		}

		static boolean doingAttack(NPC n) {
			if (n == null)
				return false;
			for (int i : ATTACK_ANIMATIONS) {
				if (n.getAnimation() == i)
					return true;
			}
			return false;
		}
	}

	static final int[] ATTACK_ANIMATIONS = { 240, 6249, 5327, 12632, 5485,
			14859, 2070, 2067 , 2062 , 2068 , 2075 , 2080 , 1163};
	/*
	 * dharok:2067
	 * verac:2062
	 * torag:2068
	 * karil:2075
	 * guthan:2080
	 * ahrim:1163
	 */

	enum ToBankChoices {
		HOUSE_PORTAL, ECTO, ANCIENTS_TELEPORT;
	}

	ToBankChoices toBankChoice;

	enum FromBankChoices {
		BAR_CELLAR, SWAMP, DRAKANS_MEDALLION;
	}

	FromBankChoices fromBankChoice;

	enum SwampTraverse {
		TO_TRAP_DOOR, TO_SECRET_WALL, TO_WOODEN_DOOR, TO_ROPE_BRIDGE, TO_BOAT_CELLAR,
		TO_SWAMP_GATE, TO_BOAT_SWAMP, TO_BARROWS;
		static final int TRAP_DOOR_ID = 5055;
		static final int SECRET_WALL_ID = 5052;
		static final int WOODEN_DOOR_ID = 30262;
		static final int ROPE_BRIDGE_TREE_ID = 5005;
		static final int BOAT_ID = 6970;
		static final int SWAMP_GATE_ID = 3507;

		static final Tile TRAP_DOOR_TILE = new Tile(3502, 3466, 0);
		static final Tile WOODEN_DOOR_TILE = new Tile(3500, 9804, 0);
		static final Tile BOAT_TILE = new Tile(3493, 3386, 0);

		static final Tile[] CELLAR_PATH = { new Tile(3500, 9812, 0),
			new Tile(3495, 9813, 0), new Tile(3491, 9815, 0),new Tile(3487, 9820, 0),
			new Tile(3484, 9825, 0),new Tile(3481, 9831, 0) };
		static final Tile[] ROPE_TO_BOAT_PATH = { new Tile(3498, 3380, 0),
			new Tile(3496, 3386, 0), new Tile(3495, 3392, 0),new Tile(3495, 3400, 0),
			new Tile(3493, 3406, 0),new Tile(3493, 3413, 0), new Tile(3496, 3419, 0),
			new Tile(3499, 3422, 0) };
		static final Tile[] BOAT_TO_BARROWS = { new Tile(3566, 3305, 0),
			new Tile(3565, 3310, 0), new Tile(3560, 3312, 0),new Tile(3554, 3310, 0),
			new Tile(3548, 3307, 0),new Tile(3542, 3301, 0), new Tile(3541, 3296, 0),
			new Tile(3538, 3290, 0), new Tile(3538, 3284, 0),new Tile(3532, 3280, 0),
			new Tile(3528, 3280, 0),new Tile(3523, 3280, 0) };
		static final Tile[] BANK_TO_SWAMP_GATE = {
			new Tile(3444,3459,0),new Tile(3447,3463,0),new Tile(3452,3468,0),
			new Tile(3457,3472,0),new Tile(3463,3473,0),new Tile(3471,3475,0),
			new Tile(3477,3476,0),new Tile(3484,3478,0),new Tile(3487,3480,0),
			new Tile(3495,3482,0),new Tile(3503,3481,0),new Tile(3507,3480,0),
			new Tile(3510,3480,0),};
		static final Tile[] GATE_TO_BOAT = {new Tile(3495,3378,0),
			new Tile(3489,3378,0),new Tile(3483,3378,0),new Tile(3477,3381,0),
			new Tile(3473,3386,0),new Tile(3472,3392,0),new Tile(3471,3400,0),
			new Tile(3467,3405,0),new Tile(3469,3413,0),new Tile(3463,3419,0),
			new Tile(3459,3424,0),new Tile(3456,3429,0),new Tile(3449,3433,0),
			new Tile(3442,3440,0),new Tile(3437,3446,0),new Tile(3434,3451,0),
			new Tile(3438,3455,0),new Tile(3441,3455,0),};
		
		static final Area CELLAR_AREA = new Area(new Tile(3469, 9848, 0),
				new Tile(3489, 9837, 0));
		static final Area SWAMP_AREA = new Area(new Tile(3504, 3452, 0),
				new Tile(3515, 3441, 0));
		static final Area MORTTON_AREA = new Area();
		static final Area BARROWS_AREA = new Area(new Tile(3554, 3307, 0),
				new Tile(3573, 3297, 0));
		static final Area SWAMP_GATE_AREA = new Area(new Tile(3441,3458,0),
				new Tile(3446,3453,0));
	}
	SwampTraverse swampState;
	
	enum MedallionTraverse{
		TO_BARROWS;
		//interact medallion "Teleport"
		//click widget
		//at barrows(north of the hut)
		
		static final int MEDALLION_ID = 21576;
		private static final int CHATBOX_WIDGET = 1188;
		private static final int OPTION_WIDGETCHILD = 2;
	}
	
	enum EctoTraverse{
		TO_ECTOFUNGUS,TO_BANK;
		
		static final int ECTOPHIAL_ID = 4251;
		
		static final Area ECTOFUNGUS_AREA = new Area(new Tile(3652,3528,0),new Tile(3668,3511,0));
		
		static final Tile[] ECTOFUNGUS_TO_BANK = {new Tile(3510,3480,0),
			new Tile(3506,3482,0),new Tile(3505,3486,0),new Tile(3503,3492,0),
			new Tile(3502,3498,0),new Tile(3506,3502,0),new Tile(3511,3509,0),
			new Tile(3515,3514,0),new Tile(3519,3519,0),new Tile(3524,3526,0),
			new Tile(3531,3526,0),new Tile(3537,3527,0),new Tile(3543,3527,0),
			new Tile(3550,3528,0),new Tile(3557,3529,0),new Tile(3564,3530,0),
			new Tile(3571,3530,0),new Tile(3578,3530,0),new Tile(3584,3531,0),
			new Tile(3589,3532,0),new Tile(3596,3534,0),new Tile(3602,3534,0),
			new Tile(3608,3533,0),new Tile(3615,3533,0),new Tile(3621,3533,0),
			new Tile(3628,3533,0),new Tile(3634,3533,0),new Tile(3638,3530,0),
			new Tile(3646,3530,0),new Tile(3652,3529,0),new Tile(3658,3529,0),
};
	}
	EctoTraverse ectoState;
	
	enum HouseTraverse{//6868,5644
		TO_HOUSE,TO_HOUSE_ALTER,TO_CANIFIS_BAR,TO_BANK;
		static final int HOUSE_TAB_ID = 8013;
		static final int ENTRY_PORTAL_ID = 13405;
		static final int[] CANIFIS_PORTAL_IDS = {13621,13628,13635};
		static final int[] DOOR_IDS = {13100,13101,13118,13119,13094,13096,
			13006,13007,13109,13107,13016,13015};
		static final int[] ALTAR_IDS = {13179,13180,13182,13184,13185,13188,13193,13194,13197,13198};//need limestone / gilded
		
		static final Area BAR_AREA = new Area(new Tile(3487,3480,0),new Tile(3505,3467,0));
		
		static SceneObject getAltarDoor(){
			final Area room = getAltarRoom();
			SceneObject result = SceneEntities.getNearest(new Filter<SceneObject>(){
				@Override
				public boolean accept(SceneObject found) {
					for(int i : DOOR_IDS){
						if(found.getId() == i && room.contains(found.getLocation()))
							return true;
					}
					return false;
				}});
			return result;
		}
		static SceneObject getPortalDoor(){
			final Area room = getPortalRoom();
			SceneObject result = SceneEntities.getNearest(new Filter<SceneObject>(){
				@Override
				public boolean accept(SceneObject found) {
					for(int i : DOOR_IDS){
						if(found.getId() == i && room.contains(found.getLocation()))
							return true;
					}
					return false;
//					return ((found.getId() == DOOR_1_ID || found.getId() == DOOR_2_ID)
//							&& room.contains(found.getLocation()));
				}});
			return result;
		}
		static SceneObject getAltar(){
			return SceneEntities.getNearest(ALTAR_IDS);
		}
		static SceneObject getPortal(){
			return SceneEntities.getNearest(CANIFIS_PORTAL_IDS);
		}
		static SceneObject getEntryPortal(){
			return SceneEntities.getNearest(ENTRY_PORTAL_ID);
		}
		static Area getAltarRoom(){
			SceneObject altar = getAltar();
			if(altar == null) return null;
			Area[] rooms = {getNorthRoom(),getWestRoom(),getSouthRoom(),getEastRoom()};
			for(Area a : rooms){
				if(a.contains(altar.getLocation()))
					return a;
			}
			return null;
		}
		static Area getPortalRoom(){
			SceneObject portal = getPortal();
			if(portal == null) return null;
			Area[] rooms = {getNorthRoom(),getWestRoom(),getSouthRoom(),getEastRoom()};
			for(Area a : rooms){
				if(a.contains(portal.getLocation()))
					return a;
			}
			return null;
		}
		static Area getNorthRoom(){
			if(getEntryPortal() == null) return null;
			Tile pt = getEntryPortal().getLocation();
			return new Area(new Tile(pt.getX()-4,pt.getY()+12,0), 
					new Tile(pt.getX()+4,pt.getY()+4,0));
		}
		static Area getWestRoom(){//(6856,5648) + (6864,5640)
			if(getEntryPortal() == null) return null;
			Tile pt = getEntryPortal().getLocation();
			return new Area(new Tile(pt.getX()-12,pt.getY()+4,0), 
					new Tile(pt.getX()-4,pt.getY()-4,0));
		}
		static Area getSouthRoom(){//(6864,5640) + (6872,5632)
			if(getEntryPortal() == null) return null;
			Tile pt = getEntryPortal().getLocation();
			return new Area(new Tile(pt.getX()-4,pt.getY()-4,0), 
					new Tile(pt.getX()+4,pt.getY()-12,0));
		}
		static Area getEastRoom(){//(6872,5648) + (6880,5640)
			if(getEntryPortal() == null) return null;
			Tile pt = getEntryPortal().getLocation();
			return new Area(new Tile(pt.getX()+4,pt.getY()+4,0), 
					new Tile(pt.getX()+12,pt.getY()-4,0));
		}
	}
	HouseTraverse houseState;
	
	enum AncientsTraverse{
		TO_BAR,TO_BANK;
		
		static final int SPELL_WIDGET = 0;
		
		static final Area BAR_AREA = new Area(new Tile(3487,3480,0),new Tile(3505,3467,0));
	}
	AncientsTraverse ancientsState;
	/*
	final int[] BARROWS_LOOT = { 565, 560, 4740, 558, 562, 1149, 987, 985,
	4708, 4712, 4714, 4710, 4716, 4720, 4722, 4718, 4745, 4749, 4751,
	4747, 4753, 4757, 4759, 4755, 4724, 4728, 4730, 4726, 4732, 4736,
	4738, 4734 };
	*/
	enum LootItem{
		BLOOD_RUNE(565),DEATH_RUNE(560),CHAOS_RUNE(562),MIND_RUNE(558),BOLT_RACK(4740),
		DRAGON_HELM(1149),LOOP_HALF(987),TOOTH_HALF(985),
		AHRIMS_HOOD(4708),AHRIMS_ROBETOP(4712),AHRIMS_ROBEBOTTOM(4714),AHRIMS_STAFF(4710),
		DHAROKS_HELM(4716),DHAROKS_PLATEBODY(4720),DHAROKS_PLATELEGS(4722),DHAROKS_GREATAXE(4718),
		TORAGS_HELM(4745),TORAGS_PLATEBODY(4749),TORAGS_PLATELEGS(4751),TORAGS_HAMMERS(4747),
		VERACS_HELM(4753),VERACS_BRASSARD(4757),VERACS_PLATESKIRT(4759),VERACS_FLAIL(4755),
		GUTHANS_HELM(4724),GUTHANS_PLATEBODY(4728),GUTHANS_CHAINSKIRT(4730),GUTHANS_SPEAR(4726),
		KARILS_COIF(4732),KARILS_TOP(4736),KARILS_BOTTOM(4738),KARILS_XBOW(4734);
		LootItem(int id){
			_id = id;
			_value = 0;
			_numRecieved = 0;
		}
		private static int[] BARROWS_IDS = {4708,4712,4714,4710,4716,4720,4722,4718,
			4745,4749,4751,4747,4753,4757,4759,4755,4724,4728,4730,4726,4732,4736,4738,4734};
		private int _id;
		private int _value;
		private int _numRecieved;
		int getId(){
			return _id;
		}
		int getValue(){
			if(_value == 0)
				try {
					_value = getPrice(_id);
				} catch (IOException e) {
				}
			return _value;
		}
		int getNumRecieved(){
			return _numRecieved;
		}
		void increaseNumRecieved(int amt){
			_numRecieved += amt;
		}
		void clearNumRecieved(){
			_numRecieved = 0;
		}
		boolean isBarrowsItem(){
			for(int i : BARROWS_IDS){
				if(_id == i)
					return true;
			}
			return false;
		}
		static LootItem getLootItem(int id){
			for(LootItem l : LootItem.values()){
				if(l.getId() == id)
					return l;
			}
			return null;
		}
	}
	
	boolean toBrother, fightBrother, toCrypt, targetRoom, setupPath, solvedPuzzle, 
	misclickPuzzle, toCryptChestDFS,doCryptChest, lootCryptChest, statsUpdated, lootUpdated, fromCryptChest, 
	toBank,doBank, fromBank,banking;
	int prayerBoostPoint, eatHealth;
	
	String status,error;

	BufferedImage cryptMap;
	BufferedImage northSidePath;
	BufferedImage eastSidePath;
	BufferedImage southSidePath;
	BufferedImage westSidePath;
	BufferedImage errorMessage;
	
	// gui selections
	public int foodId,numFood,numPrayPotion;
	public boolean start;
	boolean dharokSpecialAtt,veracSpecialAtt,toragSpecialAtt,karilSpecialAtt,guthanSpecialAtt,
	ahrimSpecialAtt,dharokProtPray,veracProtPray,toragProtPray,karilProtPray,guthanProtPray,
	ahrimProtPray;
	ArrayList<Item> prepInventory;
	
	public enum Gear{
		WEAPON(-1),SHIELD(-1),HELMET(-1),CHEST(-1),
		LEGS(-1),GLOVES(-1),BOOTS(-1),NECKLACE(-1),RING(-1),
		CAPE(-1);
		Gear(int id){
			id = _id;
		}
		private int _id;
		public void setId(int id){
			_id = id;
		}
		public int getId(){
			return _id;
		}
	}

	// variables
	LinkedList<CryptEdge> path;
	int pathIndex,numRuns,numRunsHr,numTripRuns,profit,profitHr,invProfit,numBarrowsItems,
	numBarrowsItemsHr,invNumBarrowsItems,submitProfit,submitItems,submitTrips,updateMinute;
	long startTime,millis,hours,minutes,seconds,last;
	
	CryptEdge lastEdge;
	CryptEdge currentEdge;

	Brother toFight;
	Brother cryptHole;

	Timer idleWatch;
	
	SceneObject todraw;

	@Override
	protected void setup() {
		submit(new Begin());
		provide(new Eat());
		provide(new ToTomb());
		provide(new FightBrother());
		provide(new ToCryptChestDFS());
		provide(new FightCryptChest());
		provide(new LootCryptChest());
		provide(new FromCryptChest());
		provide(new ToBank());
		provide(new DoBank());
		provide(new FromBank());
		provide(new SubmitStatistics());
	}
	private class Test extends Strategy implements Task {
		@Override
		public void run() {
		}
	}
	
	private class Begin extends Strategy implements Task {

		@Override
		public void run() {
			loadUser();
			pathIndex = numRuns = numRunsHr= numTripRuns = profit = profitHr 
					= invProfit = numBarrowsItems = numBarrowsItemsHr = invNumBarrowsItems =  0;
			updateMinute = 10;
			millis = hours = minutes = seconds = last = 0;
			startTime = System.currentTimeMillis();
			prepInventory = new ArrayList<Item>();
			toBrother = fightBrother = toCrypt = targetRoom = toCryptChestDFS = 
			misclickPuzzle = doCryptChest = lootCryptChest = lootUpdated = fromCryptChest = 
			toBank = fromBank = banking = false;
			prayerBoostPoint = Skills.getLevel(Skills.PRAYER) * 2;
			eatHealth = Skills.getLevel(Skills.CONSTITUTION) * 7;

			int x = -10 % -10;
			
			updateEquipment();
			launchGUI(new File(Environment.getStorageDirectory(),"MiscBarrows.properties"));
			while(!start){
				updateEquipment();
			}
			updateEquipment();
			
			ArrayList<Integer> missing = Equipment.getMissingEquipment();
			if((prepInventory.size()+missing.size()+numFood+numPrayPotion) > 28){
				log.info("invalid GUI selections .... >28....numFood:"+numFood);
				numFood = 28 - prepInventory.size() - missing.size()-numPrayPotion;
				log.info("new numFood:"+numFood);
			}
			checkAutoRetaliate();
			
			for(int id : Equipment.getMissingEquipment()){
				log.info("missing gear id:"+id);
			}
			
			toFight = Brother.DHAROK;
			toBrother = true;
			toBank = banking = true;

			
			//fromBank = banking = true;
			
			//toCryptChestDFS = true;

			// cryptHole = Brother.AHRIM;
			// toCrypt = true;

			Camera.setPitch(40);
			
		}
		
		private void loadUser(){
			try {
				URL submit = new URL("http://rapidbots.clanteam.com/signature/barrows/update.php?user=" 
						+ Environment.getDisplayName() + "&runTime=" + 0 + "&profit=" + 0 + "&itemGOT" + 0
						+ "&trips" + 0);
				URLConnection con = submit.openConnection();
				con.setRequestProperty("User-Agent", "| customuseragent |");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				final BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
				rd.close();
				log.info("Signatures have been updated.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void checkAutoRetaliate(){
			Tabs.ATTACK.open();
			sleep(700);
			if(Widgets.get(884).validate() 
					&& Widgets.get(884).getChild(13).getText().contains("Off")){
				Widgets.get(884).getChild(13).click(true);
				sleep(500);
			}
			Tabs.INVENTORY.open();
			sleep(700);
		}
		
		private void launchGUI(final File f){
			try{
				SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run() {
						new MiscBarrowsGUI(f);					
					}});
			}catch(Exception e){
				log.info("error loading GUI");
			}
		}
		
		private void updateEquipment(){
			Gear.WEAPON.setId(Equipment.WEAPON.getItemId());
			Gear.SHIELD.setId(Equipment.SHIELD.getItemId());
			Gear.HELMET.setId(Equipment.HEAD.getItemId());
			Gear.CHEST.setId(Equipment.TORSO.getItemId());
			Gear.LEGS.setId(Equipment.LEGS.getItemId());
			Gear.GLOVES.setId(Equipment.GLOVES.getItemId());
			Gear.BOOTS.setId(Equipment.BOOTS.getItemId());
			Gear.NECKLACE.setId(Equipment.NECK.getItemId());
//			Gear.RING.setId(Equipment.RING.getItemId());
			Gear.CAPE.setId(Equipment.CAPE.getItemId());
			
//			log.info("set weapon"+Equipment.WEAPON.getItemId());
//			log.info("set shield"+Equipment.SHIELD.getItemId());
//			log.info("set helm"+Equipment.HEAD.getItemId());
//			log.info("set body"+Equipment.TORSO.getItemId());
//			log.info("set legs"+Equipment.LEGS.getItemId());
//			log.info("set gloves"+Equipment.GLOVES.getItemId());
//			log.info("set boots"+Equipment.BOOTS.getItemId());
//			log.info("set necklace"+Equipment.NECK.getItemId());
//			log.info("set ring"+Equipment.RING.getItemId());
		}
	}

	private class Eat extends Strategy implements Task, Condition {

		@Override
		public void run() {
			if (Inventory.getCount(foodId) == 0) {
				if(lootCryptChest)
					finishLooting();
				toBank = banking = true;
				configureTask();
				return;
			}
			if(!Tabs.INVENTORY.isOpen()){
				Tabs.INVENTORY.open();
				sleep(1000);
			}
			Item food = Inventory.getItem(foodId);
			if (food != null) {
				food.getWidgetChild().interact("Eat");
				sleep(1000);
			}
		}
		
		private void finishLooting(){
			SceneObject chest = SceneEntities.getNearest(CRYPT_CHEST_ID);
			chest.click(true);
			sleep(1000);
			chest.click(true);
		}

		@Override
		public boolean validate() {
			return (!banking && getHealth() < eatHealth);
		}
	}

	private class FightBrother extends Strategy implements Task, Condition {

		@Override
		public void run() {
			if (toFight == null) {
				fightBrother = false;
				toBrother = true;
				return;
			}
			if (Players.getLocal().getPlane() != 3) {// fail safe
				fightBrother = false;
				toBrother = true;
				return;
			}
			switch (toFight) {
			case DHAROK: {
				doFight(Brother.DHAROK, Brother.VERAC, Tomb.DHAROK, MELEE_PRAYER,dharokProtPray);
				break;
			}
			case VERAC: {
				doFight(Brother.VERAC, Brother.TORAG, Tomb.VERAC, MELEE_PRAYER,veracProtPray);
				break;
			}
			case TORAG: {
				doFight(Brother.TORAG, Brother.KARIL, Tomb.TORAG, MELEE_PRAYER,toragProtPray);
				break;
			}
			case KARIL: {
				doFight(Brother.KARIL, Brother.GUTHAN, Tomb.KARIL, RANGED_PRAYER,karilProtPray);
				break;
			}
			case GUTHAN: {
				doFight(Brother.GUTHAN, Brother.AHRIM, Tomb.GUTHAN, MELEE_PRAYER,guthanProtPray);
				break;
			}
			case AHRIM: {
				doFight(Brother.AHRIM, null, Tomb.AHRIM, MAGIC_PRAYER,ahrimProtPray);
				break;
			}
			}
		}

		private void doFight(Brother current, Brother next, Tomb t, int prayerConstant, boolean doPray) {
			NPC fighting = (NPC) Players.getLocal().getInteracting();
			if (fighting != null) {
				if (fighting.getAnimation() == 836) {
					log.info(current.toString() + "dead");
					toFight = next;
					fightBrother = false;
					toBrother = true;
					return;
				}
			}
			if (doPray && getPrayerPoints() < prayerBoostPoint) {
				sipPrayerPotion();
				sleep(1500);
			}
			if (doPray){ 
				togglePrayer(prayerConstant);
				sleep(500);
			}
			if(fighting != null)
				return;
			NPC brother = getAggressor();
			if (brother == null) {
				SceneObject tomb = SceneEntities.getNearest(t.getTombId());
				if (tomb != null) {
					if (tomb.isOnScreen()) {
						if (tomb.interact("Search"))
							sleep(500);
						else {
							stepTowards(tomb);
							sleep(500);
						}
					} else {
						Camera.turnTo(tomb);
					}
					if (Widgets.get(1186).validate()) {//* KARIL: 1189 - 523(0x20b) -> 270(0x10e)
						
						cryptHole = current;
						toFight = next;
						fightBrother = false;
						toBrother = true;
					}
				}
			} else if (!brother.isInCombat()) {
				brother.interact("Attack");
				sleep(500);
			}
			if (killedListContains(current)) {
				toFight = next;
				fightBrother = false;
				toBrother = true;
			}
		}

		@Override
		public boolean validate() {
			return fightBrother && !banking;
		}

	}

	private class ToTomb extends Strategy implements Task, Condition {

		@Override
		public void run() {
			checkRun();
			if (toCrypt && detectRoom() != null) {
				log.info("room:" + detectRoom());
				toCrypt = false;
				toCryptChestDFS = true;
				return;
			}
			log.info("toBrother:" + toBrother + "  /  toCrypt:" + toCrypt
					+ "  /  toFight:" + toFight+"   cryptHole:"+cryptHole);
			if (toBrother && toFight == null) {
				toBrother = false;
				toCrypt = true;
			}
			if (Players.getLocal().getPlane() == 3) {// in a tomb
				log.info("in a tomb");
				Tomb tomb = Tomb.getTomb();
				if (tomb == null)// should never happen
					return;
				if (toBrother) {
					if (tomb.toString().equals(toFight.toString())) {
						tomb.getTombTile().interact("Walk here");
						toBrother = false;
						fightBrother = true;
						return;
					} else {
						log.info("exit tomb");
						SceneObject stairs = SceneEntities.getNearest(tomb
								.getStairsId());
						if (stairs != null) {
							if (stairs.isOnScreen()
									&& stairs.interact("Climb-up")) {
								sleep(3000);
							} else {
								Camera.turnTo(stairs);
							}
						}
					}
				} else if (toCrypt) {
					if (tomb.toString().equals(cryptHole.toString())) {
						// open crypt
						SceneObject sarc = SceneEntities.getNearest(tomb
								.getTombId());
						if (sarc != null) {
							if (sarc.isOnScreen() && sarc.interact("Search"))
								sleep(2000);
							else {
								Camera.turnTo(sarc);
							}
						}
						while (Widgets.get(CHAT_MENU_WIDGET).validate()) {
							Widgets.get(CHAT_MENU_WIDGET)
									.getChild(CHAT_MENU_FORWARD_WIDGET_CHILD)
									.click(true);
							sleep(1500);
						}
						while (Widgets.get(CHAT_OPTION_MENU_WIDGETS).validate()) {
							Widgets.get(CHAT_OPTION_MENU_WIDGETS)
									.getChild(CHAT_OPTION_MENU_OK_WIDGET_CHILD)
									.click(true);
							sleep(1500);
						}
					} else {
						SceneObject stairs = SceneEntities.getNearest(tomb.getStairsId());
						if (stairs != null) {
							if (stairs.isOnScreen()
									&& stairs.interact("Climb-up")) {
								sleep(3000);
							} else {
								Camera.turnTo(stairs);
							}
						}
					}
				}
				return;
			}
			Brother toSwitch;
			if (toBrother) {
				toSwitch = toFight;
			} else {
				toSwitch = cryptHole;
			}
			if (toBrother)
				wearEquip(toFight);
			if(toSwitch.isAutocasting() && !AttackStyles.StandardSpells.isAutoCasting())
				toFight.getSpell().setCast();
			log.info("heading to..."+toSwitch);
			switch (toSwitch) {
			case DHAROK: {
				toMound(Tomb.DHAROK);
				break;
			}
			case VERAC: {
				toMound(Tomb.VERAC);
				break;
			}
			case TORAG: {
				toMound(Tomb.TORAG);
				break;
			}
			case KARIL: {
				toMound(Tomb.KARIL);
				break;
			}
			case GUTHAN: {
				toMound(Tomb.GUTHAN);
				break;
			}
			case AHRIM: {
				toMound(Tomb.AHRIM);
				break;
			}
			}
		}

		private void toMound(Tomb t) {
			SceneObject spade = getSpade(t.getSpadeId(), t.getMoundTile());
			if (spade != null) {
				log.info("interact with spade:"+spade.getId());
				if (spade.isOnScreen() && spade.interact("Dig-with"))
					sleep(3000);
				else if (Walking.walk(t.getMoundTile())) {
					Camera.turnTo(spade);
					if (getPrayerIcon() != -1) {
						clickQP();
					}
					sleep(2000);
				}

			}
		}

		@Override
		public boolean validate() {
			return (toBrother || toCrypt) && !banking;
		}

	}

	private class ToCryptChestDFS extends Strategy implements Task, Condition {

		@Override
		public void run() {
			checkRun();
			Camera.setPitch(40);
			log.info("toCryptChestDFS:" + toCryptChestDFS);
			if(Players.getLocal().getLocation().getPlane() == 3){
				log.info("go back into crypt...");
				toCryptChestDFS = false;
				toCrypt = true;
				pathIndex = 0;
				path = null;
			}
			if (path == null) {
				log.info("BUILD PATH, path:" + path);
				buildPath();
			}
			if (C_ROOM.getArea().contains(Players.getLocal())) {
				log.info("center room detected");
				solvedPuzzle = false;
				toCryptChestDFS = false;
				doCryptChest = true;
				path = reverseListPath(path);
				pathIndex = 0;
				currentEdge = path.get(pathIndex);
				log.info("path reversed, onto chest room...");
				return;
			}
			if (currentEdge.getEnd().getArea().contains(Players.getLocal()))
				currentEdge = null;
			if (currentEdge == null) {
				if (path.size() > pathIndex) {
					++pathIndex;
					currentEdge = path.get(pathIndex);
				}
			}
			if (currentEdge.getStart().getArea().contains(Players.getLocal())) {
				log.info("to door 1");
				SceneObject door1 = SceneEntities
						.getNearest(new Filter<SceneObject>() {
							@Override
							public boolean accept(SceneObject found) {
								return (found.getId() == currentEdge.getEnter()
										.getId()
										&& found.getLocation().getX() == currentEdge
												.getEnter().getLocation()
												.getX() && found.getLocation()
										.getY() == currentEdge.getEnter()
										.getLocation().getY());
							}
						});
				if (door1 != null) {
					Camera.turnTo(door1);
					if (!door1.isOnScreen()) {
						stepTowards(door1);
					}
					if (isCenterDoor(door1) && !solvedPuzzle) {
						NPC b = Brother.getMyBrother();
						if(b != null && currentEdge.getStart().getArea().contains(b)){
							handleSpawn(getMissingKill());
							return;
						}
						log.info("it is a center door");
						openPuzzleDoor(door1);
						log.info("finished center door attempt");
					} else if (EnhancedMouse.interact(door1, "Open", 1700)) {
						sleep(1000);
					}
				}
			}
			SceneObject door2 = SceneEntities
					.getNearest(new Filter<SceneObject>() {
						@Override
						public boolean accept(SceneObject found) {
							return (found.getId() == currentEdge.getExit()
									.getId()
									&& found.getLocation().getX() == currentEdge
											.getExit().getLocation().getX() && found
									.getLocation().getY() == currentEdge
									.getExit().getLocation().getY());
						}
					});
			if (door2 != null) {
				Camera.turnTo(door2);
				NPC b = Brother.getMyBrother();
				if(b != null && currentEdge.getEnd().equals(C_ROOM)
						&& Calculations.distanceTo(b) < 5){
					handleSpawn(getMissingKill());
					return;
				}
				if (!currentEdge.getStart().getArea()
						.contains(Players.getLocal())
						&& !currentEdge.getEnd().getArea()
								.contains(Players.getLocal())) {
					currentEdge.getEnter().setVisited();
					if (inSidePassage()
							&& distanceTo2D(currentEdge.getExit().getLocation()) > 6) {
						log.info("traverse side passage, distanceTo(door2):"
								+ distanceTo2D(currentEdge.getExit()
										.getLocation()));
						for (Tile t : getSidePath()) {
							if (distanceTo2D(t) < 8) {
								log.info("tile accepted:" + t);
								if (stepOnScreen(t)) {
									sleep(300);
									break;
								}
							}
						}
					}
					log.info("try open door 2");
					if (!door2.isOnScreen()) {
						Camera.turnTo(door2);
						sleep(1000);
						log.info("door not on screen");
					} else if (EnhancedMouse.interact(door2, "Open", 1700)) {
						sleep(1000);
						stepOnScreen(currentEdge.getEnd().getCenterTile());
					}

				}
			}
			log.info("(done)toCryptChestDFS:" + toCryptChestDFS);
		}

		private void handleSpawn(Brother b){
			wearEquip(b);
			if(b.isAutocasting() && !AttackStyles.StandardSpells.isAutoCasting())
				b.getSpell().setCast();
			switch(b){
			case DHAROK:{
				fightSpawn(Brother.DHAROK, MELEE_PRAYER, dharokProtPray);
				break;
			}
			case VERAC:{
				fightSpawn(Brother.VERAC, MELEE_PRAYER, veracProtPray);
				break;
			}
			case TORAG:{
				fightSpawn(Brother.TORAG, MELEE_PRAYER, toragProtPray);
				break;
			}
			case KARIL:{
				fightSpawn(Brother.KARIL, RANGED_PRAYER, karilProtPray);
				break;
			}
			case GUTHAN:{
				fightSpawn(Brother.GUTHAN, MELEE_PRAYER, guthanProtPray);
				break;
			}
			case AHRIM:{
				fightSpawn(Brother.AHRIM, MAGIC_PRAYER, ahrimProtPray);
				break;
			}
			}
		}
		
		private void fightSpawn(Brother b, int constant, boolean doPray){
			NPC brother = Brother.getMyBrother();
			log.info("get my brother : "+brother);
			if (brother == null) 
				return;
			if (doPray && getPrayerPoints() < prayerBoostPoint) {
				sipPrayerPotion();
				sleep(1500);
			}
			if (doPray) {
				togglePrayer(constant);
				sleep(1500);
			}
			if (!brother.isInCombat() && brother.interact("Attack"))
				sleep(500);
		}
		
		@Override
		public boolean validate() {
			return toCryptChestDFS && !banking;
		}
	}

	private class FightCryptChest extends Strategy implements Task, Condition {

		@Override
		public void run() {
			log.info("do crypt chest room");
			if (detectRoom() != C_ROOM) {// fail safe
				for (CryptDoor d : C_ROOM.getDoors()) {
					if (d.isOpenable()) {// current edge will be first edge for
											// the path to get back
						SceneObject door = SceneEntities.getNearest(currentEdge
								.getEnter().getId());
						if (door.interact("Open"))
							sleep(2000);
					}
				}
			}
			Brother missingBrother = getMissingKill();
			Character fighting = Players.getLocal().getInteracting();
			if (fighting != null) {
				if (fighting.getAnimation() == 836) {
					log.info("death detected");
					sleep(5000);
					return;
				}
			}
			if (killedListFull() || !Widgets.get(KILLED_LIST_WIDGET).validate()) {
				log.info("fight crypt --> loot crypt");
				doCryptChest = false;
				lootCryptChest = true;
				idleWatch = new Timer(0);
				idleWatch.setEndIn(12000);
				return;
			}
			if (missingBrother != null) {
				wearEquip(missingBrother);
				if(missingBrother.isAutocasting() && !AttackStyles.StandardSpells.isAutoCasting())
					missingBrother.getSpell().setCast();
				log.info("handle brother");
				switch (missingBrother) {
				case DHAROK: {
					handleBrother(Brother.DHAROK, MELEE_PRAYER,dharokProtPray);
					break;
				}
				case VERAC: {
					handleBrother(Brother.VERAC, MELEE_PRAYER,veracProtPray);
					break;
				}
				case TORAG: {
					handleBrother(Brother.TORAG, MELEE_PRAYER,toragProtPray);
					break;
				}
				case KARIL: {
					handleBrother(Brother.KARIL, RANGED_PRAYER,karilProtPray);
					break;
				}
				case GUTHAN: {
					handleBrother(Brother.GUTHAN, MELEE_PRAYER,guthanProtPray);
					break;
				}
				case AHRIM: {
					handleBrother(Brother.AHRIM, MAGIC_PRAYER,ahrimProtPray);
					break;
				}
				}
			}
		}

		// TODO make it search or open
		private boolean searchChest() {
			SceneObject chest = SceneEntities.getNearest(CRYPT_CHEST_ID);
			if (chest != null) {
				if (chest.isOnScreen()) {
					if (chest.click(false)) {
						sleep(500);// TODO: check model or something, so no
									// right click
						Menu.select(Menu.contains("Open") ? "Open" : "Search");
						sleep(4000);// can't risk double click
						return true;
					}
				} else {
					Camera.turnTo(chest);
					stepTowards(chest);
				}
			}
			return false;
		}

		public void handleBrother(Brother b, int constant, boolean doPray) {
			if (doPray && getPrayerPoints() < prayerBoostPoint) {
				sipPrayerPotion();
				sleep(2000);
			}
			if (doPray) {
				togglePrayer(constant);
				sleep(1500);
			}
			NPC brother = Brother.getMyBrother();
			if (brother == null) {
				log.info("brother does not exist, search chest");
				sleep(2500);
				searchChest();
			}
			else if (!C_ROOM.getArea().contains(brother)){
				log.info("brother not in room, search chest");
				searchChest();
			}
			else if (!brother.isInCombat() && brother.interact("Attack"))
				sleep(500);
		}

		@Override
		public boolean validate() {
			return doCryptChest && !banking;
		}
	}

	private class LootCryptChest extends Strategy implements Task, Condition {

		@Override
		public void run() {
			log.info("loot chest");
			while (Inventory.getCount(VIAL_ID) > 0) {
				if(dropVial())
					sleep(1500);
			}
			if(killedListValidate())
				searchChest();
			GroundItem g = findLoot();
			String lootName = (g == null) ? "null" : g.getGroundItem().getName();
			log.info("loot item:"+lootName);
			if (g == null && !killedListValidate()) {
				log.info("loot crypt --> from crypt");
				lootCryptChest = false;
				fromCryptChest = true;
				++numRuns;
				++numTripRuns;
			} else {
				if (Inventory.getCount() == 28) {
					clearInventorySlot();
				}
				if (g.isOnScreen()) {
					if (g.interact("Take", g.getGroundItem().getName()))
						sleep(2000);
				}
			}
		}

		public boolean searchChest() {
			SceneObject chest = SceneEntities.getNearest(CRYPT_CHEST_ID);
			if (chest != null) {
				if (chest.isOnScreen()) {
					if (chest.click(true)) {
						sleep(2000);
						return true;
					}
				} else {
					Camera.turnTo(chest);
				}
			}
			return false;
		}

		@Override
		public boolean validate() {
			return lootCryptChest && !banking;
		}
	}

	private class FromCryptChest extends Strategy implements Task, Condition {

		@Override
		public void run() {
			checkRun();
			if(!lootUpdated){
				updateLoot();
			}
			if(betweenY(3000, 4000)){
				lootUpdated = false;
				fromCryptChest = false;
				cryptHole = null;
				destroyPath();
				toFight = Brother.DHAROK;
				toBrother = true;
			}
			if (Players.getLocal().getPlane() == 3) {
				Tomb tomb = Tomb.getTomb();
				if(tomb == null) return;
				log.info("exit tomb");
				SceneObject stairs = SceneEntities.getNearest(tomb.getStairsId());
				if (stairs != null) {
					if (stairs.isOnScreen()
							&& stairs.interact("Climb-up")) {
						sleep(2000);
					} else {
						Camera.turnTo(stairs);
					}
				}
			}
			if (currentEdge == null) {
				if (path.size() > pathIndex) {
					++pathIndex;
					currentEdge = path.get(pathIndex);
				} else { // we are in end room
					SceneObject rope = SceneEntities
							.getNearest(getRopeId(detectRoom()));
					log.info("in rope room, rope id:" + getRopeId(detectRoom()));
					if (rope != null) {
						if (rope.isOnScreen()) {
							if (rope.interact("Climb-up"))
								sleep(3000);
							sleep(500);
						} else {
							Camera.turnTo(rope);
							stepTowards(rope);
						}
					}
				}
			} else if (currentEdge.getEnd().getArea().contains(Players.getLocal())){
				currentEdge = null;
				return;
			}else{
				log.info("from chest room...current edge:"+currentEdge.toString());
				log.info("end:"+currentEdge.getEnd()+"contains:"+currentEdge.getEnd().getArea().contains(Players.getLocal()));
			}
			if (currentEdge.getStart().getArea().contains(Players.getLocal())) {
				log.info("to door 1");
				SceneObject door1 = SceneEntities
						.getNearest(new Filter<SceneObject>() {
							@Override
							public boolean accept(SceneObject found) {
								return (found.getId() == currentEdge.getEnter()
										.getId()
										&& found.getLocation().getX() == currentEdge
												.getEnter().getLocation()
												.getX() 
										&& found.getLocation()
										.getY() == currentEdge.getEnter()
										.getLocation().getY());
							}
						});
				if (door1 != null) {
					Camera.turnTo(door1);
					if (!door1.isOnScreen()) {
						stepTowards(door1);
					} else if (EnhancedMouse.interact(door1, "Open", 1700))
						sleep(1000);
				}
			}
			SceneObject door2 = SceneEntities
					.getNearest(new Filter<SceneObject>() {
						@Override
						public boolean accept(SceneObject found) {
							return (found.getId() == currentEdge.getExit()
									.getId()
									&& found.getLocation().getX() == currentEdge
											.getExit().getLocation().getX() && found
									.getLocation().getY() == currentEdge
									.getExit().getLocation().getY());
						}
					});
			if (door2 != null) {
				Camera.turnTo(door2);
				if (!currentEdge.getStart().getArea()
						.contains(Players.getLocal())
						&& !currentEdge.getEnd().getArea()
								.contains(Players.getLocal())) {
					currentEdge.getEnter().setVisited();
					if (inSidePassage()
							&& distanceTo2D(currentEdge.getExit().getLocation()) > 6) {
						log.info("traverse side passage, distanceTo(door2):"
								+ distanceTo2D(door2.getLocation()));
						for (Tile t : getSidePath()) {
							if (distanceTo2D(t) < 8) {
								log.info("tile accepted:" + t);
								if (stepOnScreen(t)) {
									sleep(300);
									break;
								}
							}
						}
					}
					log.info("try open door 2");
					if (!door2.isOnScreen()) {
						Camera.turnTo(door2);
						sleep(1000);
						log.info("door not on screen");
					} else if (EnhancedMouse.interact(door2, "Open", 1700)) {
						sleep(1000);
						stepOnScreen(currentEdge.getEnd().getCenterTile());
					}
				}
			}
		}

		private int getRopeId(CryptRoom c) {
			if (c == NW_ROOM)
				return NW_ROPE_ID;
			if (c == NE_ROOM)
				return NE_ROPE_ID;
			if (c == SW_ROOM)
				return SW_ROPE_ID;
			if (c == SE_ROOM)
				return SE_ROPE_ID;
			return -1;
		}

		private void updateLoot(){
			int updatedProfit = 0;
			for(Item i : Inventory.getItems()){
				log.info("updating/loading prices...may take a few seconds");
				LootItem lootitem = LootItem.getLootItem(i.getId());
				if(lootitem != null){
					updatedProfit += i.getStackSize() * lootitem.getValue();
					if(lootitem.isBarrowsItem() 
							&& lootitem.getNumRecieved() < Inventory.getCount(lootitem.getId()))
							++numBarrowsItems;
					lootitem.increaseNumRecieved(i.getStackSize());
				}
			}
			profit += updatedProfit - invProfit;
			invProfit = updatedProfit;
			lootUpdated = true;
		}

		@Override
		public boolean validate() {
			return fromCryptChest && !banking;
		}
	}

	private class ToBank extends Strategy implements Task, Condition {

		@Override
		public void run() {
			log.info("to bank:"+toBank);
			if(!statsUpdated){
				updateStats();
			}
			if(getPrayerIcon() != -1)
				clickQP();
			if(BANK_AREA.contains(Players.getLocal())){
				statsUpdated = false;
				toBank = false;
				doBank = true;
				ectoState = null;
				houseState = null;
				ancientsState = null;
				return;
			}
			checkRun();
			switch (toBankChoice) {
			case ECTO: {
				traverseEcto();
				break;
			}
			case HOUSE_PORTAL: {
				Camera.setPitch(15);
				traverseHouse();
				break;
			}
			case ANCIENTS_TELEPORT: {
				ancientsTraverse();
				break;
			}
			}

		}
		
		private void updateStats(){
			for(LootItem i : LootItem.values()){
				i.clearNumRecieved();
			}
			numTripRuns = invProfit = 0;
			statsUpdated = true;
		}

		@Override
		public boolean validate() {
			return toBank;
		}
	}
	
	private class DoBank extends Strategy implements Task, Condition{

		@Override
		public void run() {
			log.info("do bank");
			if(validateInventory()){
				doBank = false;
				fromBank = true;
				error = null;
				return;
			}
			if(Inventory.getCount(14664) > 0){
				if(Bank.isOpen() && Bank.close())
					sleep(1000);
				Inventory.getItem(14664).getWidgetChild().interact("Drop");
				sleep(1000);
			}
			log.info("bank is open:"+Bank.isOpen());
			if(!Bank.isOpen() && Bank.open())
				sleep(2000);
			if(Inventory.getCount() > 0)
				depositAll();
			sleep(1000);
			if(getHealth() < Skills.getRealLevel(Skills.CONSTITUTION) * 9)
				restoreHealth();
			if(getPrayerPoints() < Skills.getRealLevel(Skills.PRAYER) * 8)
				restorePrayer();
			if(!Bank.isOpen() && Bank.open())
				sleep(2000);
			for(int gearId : Equipment.getMissingEquipment()){
				log.info("withdraw a missing equipment piece: "+gearId);
				if(Equipment.Multiple.getMultiple(gearId) != null){
					for(int i : Equipment.Multiple.getMultiple(gearId).getIds()){
						if(Bank.getItemCount(i) > 0)
							gearId = i;
					}
				}
				if(Bank.getItem(gearId) == null)
					error = "Missing Gear, ID: "+gearId;
				if(Bank.withdraw(gearId, 1)){
					sleep(2000);
				}
				if(Equipment.Multiple.getMultiple(gearId) == Equipment.Multiple.POLYPORE_STAFF
						&& !Equipment.Multiple.isWearing(gearId)){
					Inventory.getItem(gearId).getWidgetChild().click(false);
					sleep(1000);
					Menu.select(Menu.contains("Wear") ? "Wear":"Wield");
					sleep(1000);
					return;
				}
			}
			for(Item item : prepInventory){
				if(Bank.getItem(item.getId()) == null)
					error = "Missing Item, ID:"+item.getId();
				if(Bank.withdraw(item.getId(), item.getStackSize()))
					sleep(1000);
			}
			if(Bank.getItem(PRAYER_POTION_4_ID) == null ||
					Bank.getItem(PRAYER_POTION_4_ID).getStackSize() < numPrayPotion)
				error = "Missing Prayer Potions";
			if(Bank.withdraw(PRAYER_POTION_4_ID,numPrayPotion))
				sleep(1000);
			if(Bank.getItem(foodId) == null ||
					Bank.getItem(foodId).getStackSize() < numFood)
				error = "Missing Food, ID: "+foodId;
			if(Bank.withdraw(foodId, numFood))
				sleep(1000);
		}

		@Override
		public boolean validate() {
			return doBank;
		}

		private boolean depositAll(){
			if(!Widgets.get(762).validate()) return false;
			return(Widgets.get(762).getChild(34).click(true));
		}

		public boolean validateInventory(){
			for(Item item : prepInventory){
				log.info("check id:"+item.getId());
				if(Inventory.getCount(item.getId()) == 0)
					return false;
				if(Inventory.getItem(item.getId()).getStackSize() !=  item.getStackSize()){
					log.info("verify prepinv:"+item.getId()+" # req: "+item.getStackSize()+"   actual:"+Inventory.getItem(item.getId()).getStackSize());
					return false;
				}
			}
			ArrayList<Integer> missing = Equipment.getMissingEquipment();
			for(int i : missing){
				log.info("verify missing equipment:"+i);
				if(Inventory.getCount(i) != 1)
					return false;
			}
			if(Inventory.getCount(PRAYER_POTION_4_ID) != numPrayPotion)
				return false;
			int newNumFood = numFood;
			if((prepInventory.size()+missing.size()+numFood+numPrayPotion) > 28)
				newNumFood = 28 - prepInventory.size() - missing.size()-numPrayPotion;
			return (Inventory.getCount(foodId) == newNumFood);
		}
		
		public void restoreHealth(){
			Bank.withdraw(foodId, 10);
			sleep(2000);
			for(int i = 0; i < 10 && getHealth() < Skills.getRealLevel(Skills.CONSTITUTION) * 10; i++){
				if(Inventory.getItem(foodId).getWidgetChild().interact("Eat"))
					sleep(2300);
			}
			log.info("done eating");
			depositAll();
		}
		
		public void restorePrayer(){
			Bank.withdraw(PRAYER_POTION_4_ID, 1);
			sleep(1000);
			Bank.close();
			sleep(1000);
			for(int i = 0; i < 4 && getPrayerPoints() < Skills.getRealLevel(Skills.PRAYER) * 8; i++){
				if(sipPrayerPotion())
					sleep(1500);
			}
			depositAll();
		}
	}

	private class FromBank extends Strategy implements Task, Condition {

		@Override
		public void run() {
			checkRun();
			Camera.setPitch(40);
			switch (fromBankChoice) {
			case BAR_CELLAR: {
				traverseSwamp();
				break;
			}
			case SWAMP: {
				traverseSwamp();
				break;
			}
			}

		}

		@Override
		public boolean validate() {
			return fromBank;
		}

	}

	private class SubmitStatistics extends Strategy implements Task{

		@Override
		public void run() {
			//submitProfit,submitItems,submitTrips
			log.info("submitting statistics...minutes:"+minutes);
			int incrementProfit = profit - submitProfit;
			int incrementItems = numBarrowsItems - submitItems;
			int incrementTrips = numRuns - submitTrips;
			submitProfit = profit;
			submitItems = numBarrowsItems;
			submitTrips = numRuns;
			try {
				URL submit = new URL("http://rapidbots.clanteam.com/signature/barrows/update.php?user=" 
						+ Environment.getDisplayName() + "&runTime=" + 600 + "&profit=" + incrementProfit + "&itemGOT=" + incrementItems
						+ "&trips=" + incrementTrips);
				URLConnection con = submit.openConnection();
				con.setRequestProperty("User-Agent", "| customuseragent |");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				final BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
				rd.close();
				log.info("Signatures have been updated.");
			} catch (Exception e) {
				log.info(e.getMessage());
			}
			try {
				URL submit = new URL("http://rapidbots.clanteam.com/signature/barrows/update.php?user=" 
						+ "ALL_USERS" + "&runTime=" + 600 + "&profit=" + incrementProfit + "&itemGOT=" + incrementItems
						+ "&trips=" + incrementTrips);
				URLConnection con = submit.openConnection();
				con.setRequestProperty("User-Agent", "| customuseragent |");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				final BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
				rd.close();
				log.info("Signatures have been updated.");
			} catch (Exception e) {
				log.info(e.getMessage());
			}


			updateMinute = (updateMinute == 50) ? 0 : updateMinute + 10;
		}

		@Override
		public boolean validate() {
			return start && minutes == updateMinute;
		}
	}

	private void sleep(int mili) {
		Time.sleep(mili);
	}

	private CryptEdge createEdge(CryptDoor enter, CryptRoom start) {
		if (enter == NW_N_DOOR)
			return new CryptEdge(enter, NE_N_DOOR, start, NE_ROOM);
		if (enter == NW_W_DOOR)
			return new CryptEdge(enter, SW_W_DOOR, start, SW_ROOM);
		if (enter == NW_S_DOOR)
			return new CryptEdge(enter, W_N_DOOR, start, W_ROOM);
		if (enter == NW_E_DOOR)
			return new CryptEdge(enter, N_W_DOOR, start, N_ROOM);
		if (enter == N_W_DOOR)
			return new CryptEdge(enter, NW_E_DOOR, start, NW_ROOM);
		if (enter == N_S_DOOR)
			return new CryptEdge(enter, C_N_DOOR, start, C_ROOM);
		if (enter == N_E_DOOR)
			return new CryptEdge(enter, NE_W_DOOR, start, NE_ROOM);
		if (enter == NE_N_DOOR)
			return new CryptEdge(enter, NW_N_DOOR, start, NW_ROOM);
		if (enter == NE_W_DOOR)
			return new CryptEdge(enter, N_E_DOOR, start, N_ROOM);
		if (enter == NE_S_DOOR)
			return new CryptEdge(enter, E_N_DOOR, start, E_ROOM);
		if (enter == NE_E_DOOR)
			return new CryptEdge(enter, SE_E_DOOR, start, SE_ROOM);
		if (enter == W_N_DOOR)
			return new CryptEdge(enter, NW_S_DOOR, start, NW_ROOM);
		if (enter == W_S_DOOR)
			return new CryptEdge(enter, SW_N_DOOR, start, SW_ROOM);
		if (enter == W_E_DOOR)
			return new CryptEdge(enter, C_W_DOOR, start, C_ROOM);
		if (enter == C_N_DOOR)
			return new CryptEdge(enter, N_S_DOOR, start, N_ROOM);
		if (enter == C_W_DOOR)
			return new CryptEdge(enter, W_E_DOOR, start, W_ROOM);
		if (enter == C_S_DOOR)
			return new CryptEdge(enter, S_N_DOOR, start, S_ROOM);
		if (enter == C_E_DOOR)
			return new CryptEdge(enter, E_W_DOOR, start, E_ROOM);
		if (enter == E_N_DOOR)
			return new CryptEdge(enter, NE_S_DOOR, start, NE_ROOM);
		if (enter == E_W_DOOR)
			return new CryptEdge(enter, C_E_DOOR, start, C_ROOM);
		if (enter == E_S_DOOR)
			return new CryptEdge(enter, SE_N_DOOR, start, SE_ROOM);
		if (enter == SW_N_DOOR)
			return new CryptEdge(enter, W_S_DOOR, start, W_ROOM);
		if (enter == SW_W_DOOR)
			return new CryptEdge(enter, NW_W_DOOR, start, NW_ROOM);
		if (enter == SW_S_DOOR)
			return new CryptEdge(enter, SE_S_DOOR, start, SE_ROOM);
		if (enter == SW_E_DOOR)
			return new CryptEdge(enter, S_W_DOOR, start, S_ROOM);
		if (enter == S_N_DOOR)
			return new CryptEdge(enter, C_S_DOOR, start, C_ROOM);
		if (enter == S_W_DOOR)
			return new CryptEdge(enter, SW_E_DOOR, start, SW_ROOM);
		if (enter == S_E_DOOR)
			return new CryptEdge(enter, SE_W_DOOR, start, SE_ROOM);
		if (enter == SE_N_DOOR)
			return new CryptEdge(enter, E_S_DOOR, start, E_ROOM);
		if (enter == SE_W_DOOR)
			return new CryptEdge(enter, S_E_DOOR, start, S_ROOM);
		if (enter == SE_S_DOOR)
			return new CryptEdge(enter, SW_S_DOOR, start, SW_ROOM);
		if (enter == SE_E_DOOR)
			return new CryptEdge(enter, NE_E_DOOR, start, NE_ROOM);
		return null;
	}

	private CryptRoom detectRoom() {
		Tile t = Players.getLocal().getLocation();
		if (NW_ROOM.getArea().contains(t))
			return NW_ROOM;
		if (N_ROOM.getArea().contains(t))
			return N_ROOM;
		if (NE_ROOM.getArea().contains(t))
			return NE_ROOM;
		if (W_ROOM.getArea().contains(t))
			return W_ROOM;
		if (C_ROOM.getArea().contains(t))
			return C_ROOM;
		if (E_ROOM.getArea().contains(t))
			return E_ROOM;
		if (SW_ROOM.getArea().contains(t))
			return SW_ROOM;
		if (S_ROOM.getArea().contains(t))
			return S_ROOM;
		if (SE_ROOM.getArea().contains(t))
			return SE_ROOM;
		return null;
	}
	
	private GroundItem findLoot() {
		GroundItem found = GroundItems.getNearest(new Filter<GroundItem>() {
			@Override
			public boolean accept(GroundItem g) {
				for (final int r : BARROWS_LOOT) {
					if (g.getId() == r && Calculations.distanceTo(g) < 3)
						return true;
				}
				return false;
			}
		});
		return found;
	}

	private boolean dropVial() {
		if (Inventory.getCount(VIAL_ID) > 0) {
			return Inventory.getItem(VIAL_ID).getWidgetChild().interact("Drop");
		}
		if (Inventory.getCount(14664) > 0){
			return Inventory.getItem(14664).getWidgetChild().interact("Drop");
		}
		return false;
	}

	private boolean clearInventorySlot() {
		Item toDrop = getDropItem();
		if (toDrop != null)
			return (toDrop.getWidgetChild().interact("Drop"));
		if (Inventory.getCount(foodId) * 2 > Inventory.getCount(
				PRAYER_POTIONS[0], PRAYER_POTIONS[1], PRAYER_POTIONS[2],
				PRAYER_POTIONS[3]))
			return eatFood();
		Item potion = getPotion(PRAYER_POTIONS);
		if (potion != null)
			return (potion.getWidgetChild().interact("Drop"));
		return false;
	}

	private Item getDropItem() {
		// TODO: add the 2 dose potion drops
		for (int i : JUNK_IDS) {
			if (Inventory.getCount(i) > 0)
				return Inventory.getItem(i);
		}
		return null;
	}
	
	private boolean sipPrayerPotion(){
		Item potion = getPotion(PRAYER_POTIONS);
		if(potion == null){
			toBank = banking = true;
			configureTask();
			return false;
		}
		if(!Tabs.INVENTORY.isOpen()){
			Tabs.INVENTORY.open();
			sleep(700);
		}
		return (potion.getWidgetChild().interact("Drink"));
	}

	private boolean sipPotion(int[] ids) {
		Item potion = getPotion(ids);
		if (potion != null) {
			return (potion.getWidgetChild().interact("Drink"));
		}
		return false;
	}

	private boolean eatFood() {
		if (Inventory.getCount(foodId) == 0)
			return false;
		return (Inventory.getItem(foodId).getWidgetChild().interact("Eat"));
	}

	private Item getPotion(int[] ids) {
		for (int i : ids) {
			Item potion = Inventory.getItem(i);
			if (potion != null)
				return potion;
		}
		return null;
	}

	private void wearEquip(Brother b) {
		if(Equipment.appearanceContains(POLYPORE_STICK)){
			toBank = banking = true;
			configureTask();
		}
		for (int gearId : b.getMissingGearIds()) {
			if(Equipment.Multiple.getMultiple(gearId) != null){
				for(int mid : Equipment.Multiple.getMultiple(gearId).getIds()){
					if(Inventory.getCount(mid) > 0)
						gearId = mid;
				}
			}
			if (Inventory.getCount(gearId) > 0) {// if 2h, drop?
				if (Inventory.getItem(gearId).getWidgetChild().click(true))
					Time.sleep(800);
			}
		}
	}

	private boolean stepTowards(Locatable object) {
		if (object.getLocation().isOnScreen())
			return (stepOnScreen(object.getLocation()));
		int plane = object.getLocation().getPlane();
		int x1 = Players.getLocal().getLocation().getX();
		int x2 = object.getLocation().getX();
		int y1 = Players.getLocal().getLocation().getY();
		int y2 = object.getLocation().getY();
		double mpx = (x1 + x2) / 2.0;
		double mpy = (y1 + y2) / 2.0;
		Tile to = new Tile((int) mpx, (int) mpy, plane);
		if (to.isOnScreen())
			return (stepOnScreen(to));
		return false;
	}

	private boolean stepOnScreen(Locatable object) {
		Tile toWalk = object.getLocation();
		if (!toWalk.isOnScreen()) {
			Camera.turnTo(toWalk);
			sleep(1500);
		}
		return (toWalk.interact("Walk here"));
	}

	private boolean stepPath(Tile[] revpath) {
		for (Tile t : revpath) {
			if (t.isOnMap()) {
				return (Walking.walk(t));
			}
		}
		return false;
	}

	private boolean walkTileMM(Tile tile, int rnd) {
		float angle = angleTo(tile) - Camera.getAngleTo(0);
		float distance = distanceTo(tile);
		if (distance > 18)
			distance = 18;
		angle = (float) (angle * Math.PI / 180);
		int x = 627, y = 85;
		int dx = (int) (4 * (distance + Random.nextGaussian(0, rnd, 1)) * Math
				.cos(angle));
		int dy = (int) (4 * (distance + Random.nextGaussian(0, rnd, 1)) * Math
				.sin(angle));
		return Mouse.click(x + dx, y - dy, true);
	}

	private float distanceTo(Tile tile) {
		return (float) Calculations.distance(Players.getLocal().getLocation(),
				tile);
	}
	
	private void configureTask(){
		if(fightBrother){
			//remember who to fight
			toBrother = true;
			fightBrother = false;
		}
		if(toCrypt || toBrother){
			//will remember crypt/brother we were going to, unlikely to bank while doing this
			//requires no change
		}
		if(toCryptChestDFS || doCryptChest){
			//just remember the cryptHole and regenerate path etc.
			//cryptHole should be correct
			toCryptChestDFS = doCryptChest = lootCryptChest = false;
			toCrypt = true;
			destroyPath();
		}
		if(fromCryptChest || lootCryptChest){
			//same as if we were done with a run, destroypath, reset cryptHole, etc.
			//if we were foced to immediatly bank from food shortage, it finishes looting,
			//so reset
			fromCryptChest = false;
			cryptHole = null;
			destroyPath();
			toFight = Brother.DHAROK;
			toBrother = true;
		}
	}

	private boolean betweenX(int min, int max) {
		int x = Players.getLocal().getLocation().getX();
		return (x > min && x < max);
	}

	private boolean betweenY(int min, int max) {
		int y = Players.getLocal().getLocation().getY();
		return (y > min && y < max);
	}

	private double distanceTo2D(Tile tile) {
		Tile player = Players.getLocal().getLocation();
		int a = tile.getX() - player.getX();
		int b = tile.getY() - player.getY();
		return (Math.sqrt((a * a) + (b * b)));
	}

	private int angleTo(Tile tile) {
		final double ydif = tile.getY()
				- Players.getLocal().getLocation().getY();
		final double xdif = tile.getX()
				- Players.getLocal().getLocation().getX();
		return (int) (Math.atan2(ydif, xdif) * 180 / Math.PI);
	}

	private SceneObject getSpade(final int id, final Tile t) {
		SceneObject spade = SceneEntities.getNearest(new Filter<SceneObject>() {
			@Override
			public boolean accept(SceneObject found) {
				return (found.getId() == id
						&& found.getLocation().getX() == t.getX() && found
						.getLocation().getY() == t.getY());
			}
		});
		return spade;
	}

	private boolean inSidePassage() {
		if (currentEdge.getEnter() == null)
			return false;
		int doorId = currentEdge.getEnter().getId();
		return (doorId == 6735 || doorId == 6742 || doorId == 6750 || doorId == 6736);
	}

	private Tile[] getSidePath() {
		// TODO
		int doorId = currentEdge.getEnter().getId();
		CryptRoom startRoom = currentEdge.getStart();
		if (doorId == 6735) {// north
			if (startRoom == NW_ROOM)
				return NORTH_TUNNEL_PATH;
			if (startRoom == NE_ROOM)
				return reverseTilePath(NORTH_TUNNEL_PATH);
		}
		if (doorId == 6742) {// east
			if (startRoom == NE_ROOM)
				return EAST_TUNNEL_PATH;
			if (startRoom == SE_ROOM)
				return reverseTilePath(EAST_TUNNEL_PATH);
		}
		if (doorId == 6750) {// south
			if (startRoom == SE_ROOM)
				return SOUTH_TUNNEL_PATH;
			if (startRoom == SW_ROOM)
				return reverseTilePath(SOUTH_TUNNEL_PATH);
		}
		if (doorId == 6736) {// west
			if (startRoom == SW_ROOM)
				return WEST_TUNNEL_PATH;
			if (startRoom == NW_ROOM)
				return reverseTilePath(WEST_TUNNEL_PATH);
		}
		log.info("not in side tunnel, return null sidepath");
		return null;
	}

	private String pathToString(LinkedList<CryptEdge> path) {
		if (path == null)
			return "";
		String result = "Path>>>>>>>+\n";
		for (CryptEdge e : path) {
			result += e.toString() + "\n";
		}
		result += "<<<<<<<<<<<<<";
		return result;
	}

	private String edgesToString() {
		String result = "Edges>>>>>>> + \n";
		for (CryptRoom r : rooms) {
			result += "\nFor " + r.toString() + " edges.size:"
					+ r.getEdgesCW().size() + "\n";
			for (CryptEdge e : r.getEdgesCW()) {
				result += e.toString() + "\n";
			}
		}
		result += "<<<<<<<<<<<";
		return result;
	}

	private boolean buildPath() {
		CryptRoom room = detectRoom();
		if (room == null)
			return false;
		putEdges();
		path = findPath(room);
		currentEdge = path.get(0);
		pathIndex = 0;
		return (path != null);
	}

	private void destroyPath() {
		clearEdges();
		path = null;
		currentEdge = null;
		pathIndex = 0;
	}

	private Tile[] reverseTilePath(Tile[] path) {
		Tile[] result = new Tile[path.length];
		int i = 0;
		for (int j = path.length - 1; j >= 0; --j, ++i) {
			result[i] = path[j];
		}
		return result;
	}

	private LinkedList<CryptEdge> findPath(CryptRoom r) {
		ArrayList<CryptRoom> visited = new ArrayList<CryptRoom>();
		LinkedList<CryptEdge> path = new LinkedList<CryptEdge>();
		doFindPath(r, visited, path, false);
		removeTraceBacks(path, r);
		return path;
	}

	private boolean doFindPath(CryptRoom r, ArrayList<CryptRoom> visited,
			LinkedList<CryptEdge> path, boolean done) {
		visited.add(r);
		for (CryptEdge to : r.getEdgesCW()) {
			if (!visited.contains(to.getEnd())) {
				if (done)
					break;
				path.add(to);
				if (to.getEnd() == C_ROOM) {
					doFindPath(to.getEnd(), visited, path, true);
					return true;
				} else {
					done = doFindPath(to.getEnd(), visited, path, done);
				}
			} else
				continue;
		}
		if (!done && visited.size() > 1) {
			visited.remove(visited.size() - 2);// go back and reset the vertex
			path.removeLast();
		}
		return done;
	}

	private void removeTraceBacks(LinkedList<CryptEdge> path, CryptRoom begin) {
		LinkedList<CryptEdge> removals = new LinkedList<CryptEdge>();
		for (CryptEdge ref : path) {
			for (CryptEdge crossref : path) {
				if (ref.getReverse().equals(crossref)
						&& ref.getStart() != begin
						&& crossref.getStart() != begin) {
					removals.add(ref);
					removals.add(crossref);
				}
			}
		}
		for (CryptEdge toRemove : removals) {
			path.remove(toRemove);
		}
	}

	private void putEdges() {
		for (CryptRoom r : rooms) {
			for (CryptDoor d : r.getDoors()) {
				if (d.isOpenable()) {
					r.addEdge(createEdge(d, r));
				}
			}
		}
	}

	private void clearEdges() {
		for (CryptRoom r : rooms) {
			r.clearEdges();
		}
	}

	private LinkedList<CryptEdge> getEdges() {
		LinkedList<CryptEdge> result = new LinkedList<CryptEdge>();
		for (CryptRoom r : rooms)
			for (CryptEdge e : r.getEdgesCW())
				result.add(e);
		return result;
	}

	private LinkedList<CryptEdge> reverseListPath(LinkedList<CryptEdge> l) {
		LinkedList<CryptEdge> result = new LinkedList<CryptEdge>();
		for (int i = l.size() - 1; i >= 0; --i) {
			result.add(l.get(i).getReverse());
		}
		return result;
	}

	private boolean isCenterDoor(SceneObject door) {
		return (door.getId() == N_S_DOOR.getId()
				|| door.getId() == S_N_DOOR.getId()
				|| door.getId() == W_E_DOOR.getId() || door.getId() == E_W_DOOR
				.getId());
	}

	private void openPuzzleDoor(SceneObject door) {
		Mouse.move(door.getCentralPoint().x, door.getCentralPoint().y);
		waitNextAttack();
		if (!Widgets.get(PUZZLE_WIDGET).validate()) {
			if(!door.isOnScreen()){
				stepTowards(door);
				sleep(1500);
			}
			door.interact("Open");
			log.info("puzzle not visible, open door and move mouse near");
			moveMouseNear(250, 190);
			for (int i = 0; !Widgets.get(PUZZLE_WIDGET).validate() && i < 100; i++)
				Time.sleep(20);
		}
		if (Widgets.get(PUZZLE_WIDGET).validate()) {
			log.info("puzzle ready to solve");
			Point answer = puzzleAnswer();
			Mouse.move(answer.x, answer.y);
			if (Mouse.click(true))
				sleep(1500);
		}
	}

	private void waitNextAttack() {
		NPC aggressor = getAggressor();
		if (aggressor == null)
			return;
		int i = 0;
		while (aggressor != null && !CryptMonster.doingAttack(aggressor)) {
			i++;
			Time.sleep(20);
			aggressor = getAggressor();
			if (aggressor == null || i > 150)
				break;
		}
	}

	private void sneakPuzzleDoor(SceneObject door) {
		// TODO
		log.info("puzzle door.");
		NPC aggressor = getAggressor();
		if (aggressor == null) {
			door.interact("Open");
		} else {
			while (!CryptMonster.doingAttack(aggressor))
				sleep(50);
			door.interact("Open");
		}
		for (int i = 0; !Widgets.get(PUZZLE_WIDGET).validate() && i < 100; i++) {
			Time.sleep(20);
		}
		if (!Widgets.get(PUZZLE_WIDGET).validate())
			return;
		if (Widgets.get(PUZZLE_WIDGET).validate())
			Mouse.click(puzzleAnswer(), true);
	}

	private Point puzzleAnswer() {
		if (Widgets.get(PUZZLE_WIDGET).validate()) {
			for (int child : PUZZLE_WIDGET_CHILD_CHOICES) {
				for (int answer : PUZZLE_MODEL_ANSWERS) {
					if (Widgets.get(PUZZLE_WIDGET).getChild(child).getModelId() == answer) {
						Point cp = Widgets.get(PUZZLE_WIDGET).getChild(child)
								.getCentralPoint();
						int random = Random.nextInt(-10, -10);
						return new Point((int) (cp.getX() + random),
								(int) (cp.getY() + random));
					}
				}
			}
		}
		return null;
	}
	
	private boolean killedListContains(Brother b) {
		if (!Widgets.get(KILLED_LIST_WIDGET).validate())
			return false;
		String brotherName = formatName(b.toString());
		String killedList = Widgets.get(24).getChild(3).getText();
		return (killedList.contains(brotherName));
	}

	private boolean killedListMissing(Brother b) {
		if (!Widgets.get(KILLED_LIST_WIDGET).validate())
			return false;
		String brotherName = formatName(b.toString());
		String killedList = Widgets.get(24).getChild(3).getText();
		return (!killedList.contains(brotherName));
	}

	private String formatName(String s) {
		return ("" + s.charAt(0) + s.substring(1).toLowerCase());
	}

	private Brother getMissingKill() {
		Brother result = null;
		int numMissing = 0;
		for (Brother b : Brother.values()) {
			if (killedListMissing(b)){
				result = b;
				++numMissing;
			}
		}
		if(numMissing > 1) return null;
		return result;
	}

	private NPC getAggressor() {
		NPC monster = NPCs.getNearest(new Filter<NPC>() {
			@Override
			public boolean accept(NPC found) {
				if (found.getInteracting() == null)
					return false;
				return (found.getInteracting().getId() == Players.getLocal()
						.getId());
			}
		});
		return monster;
	}
	
	private boolean killedListValidate(){
		return Widgets.get(KILLED_LIST_WIDGET).validate();
	}

	private boolean killedListEmpty() {
		if (!Widgets.get(KILLED_LIST_WIDGET).validate())
			return false;
		String killedList = Widgets.get(24).getChild(3).getText();
		return killedList.equals("None");
	}

	private boolean killedListFull() {
		if (!Widgets.get(KILLED_LIST_WIDGET).validate())
			return false;
		for (Brother b : Brother.values()) {
			if (!killedListContains(b)){
				log.info("kill list missing:"+b);
				return false;
			}
		}
		return true;
	}

	private int getHealth() {
		return Integer.parseInt(Widgets.get(748, 8).getText());
	}

	private int getPrayerPoints() {
		return Integer.parseInt(Widgets.get(749).getChild(6).getText());
	}

	private int getPrayerIcon() {
		return Players.getLocal().getPrayerIcon();
	}

	private int getCurrentPrayerWidget() {
		int icon = getPrayerIcon();
		if (icon == MAGIC_PRAYER_ICON)
			return MAGIC_PRAYER_WIDGET_CHILD;
		if (icon == RANGED_PRAYER_ICON)
			return RANGED_PRAYER_WIDGET_CHILD;
		if (icon == MELEE_PRAYER_ICON)
			return MELEE_PRAYER_WIDGET_CHILD;
		return -1;
	}
	
	private void clickQP() {
		boolean twice = true;
//		if (getPrayerIcon() == MELEE_PRAYER_ICON)
//			twice = false;
		WidgetChild qp = Widgets.get(QUICK_PRAYER_WIDGET,
				QUICK_PRAYER_WIDGET_CHILD);
		qp.click(true);
		if (twice) {
			sleep(100);
			qp.click(true);
		}
	}
	
	private void togglePrayer(int prayer) {
		int icon = Players.getLocal().getPrayerIcon();
		if(Prayer.isCursesOn()){
			switch(prayer){
			case MAGIC_PRAYER:{
				if(icon == Prayer.Type.DEFLECT_MAGIC.getIcon()) return;
				Prayer.Type.DEFLECT_MAGIC.enable(true);
				break;
			}
			case RANGED_PRAYER:{
				if(icon == Prayer.Type.DEFLECT_RANGED.getIcon()) return;
				Prayer.Type.DEFLECT_RANGED.enable(true);
				break;
			}
			case MELEE_PRAYER:{
				if(icon == Prayer.Type.DEFLECT_MELEE.getIcon()) return;
				Prayer.Type.DEFLECT_MELEE.enable(true);
				break;
			}
			}
		}
		else{
			switch(prayer){
			case MAGIC_PRAYER:{
				if(icon == Prayer.Type.PROTECT_MAGIC.getIcon()) return;
				Prayer.Type.PROTECT_MAGIC.enable(true);
				break;
			}
			case RANGED_PRAYER:{
				if(icon == Prayer.Type.PROTECT_RANGED.getIcon()) return;
				Prayer.Type.PROTECT_RANGED.enable(true);
				break;
			}
			case MELEE_PRAYER:{
				if(icon == Prayer.Type.PROTECT_MELEE.getIcon()) return;
				Prayer.Type.PROTECT_MELEE.enable(true);
				break;
			}
			}
		}
	}

//	private void togglePrayer(int typeWidget) {
//		if (getPrayerPoints() == 0)
//			return;
//		Tabs.PRAYER.open();
//		sleep(700);
//		if (!Widgets.get(PRAYERS_WIDGET).validate())
//			return;
//		if(Widgets.get(PRAYERS_WIDGET,PRAYER_ICONS_WIDGET).validate()){
//				Widgets.get(PRAYERS_WIDGET).getChild(PRAYER_ICONS_WIDGET).getChild(typeWidget).click(true);
//		}
//		else if(Widgets.get(PRAYERS_WIDGET,CURSE_ICONS_WIDGET).validate()){//curses
//			switch(typeWidget){
//			case MAGIC_PRAYER_WIDGET_CHILD:{
//				Widgets.get(PRAYERS_WIDGET,CURSE_ICONS_WIDGET).getChild(MAGIC_DEFLECT_WIDGET_CHILD).click(true);
//				break;
//			}
//			case RANGED_PRAYER_WIDGET_CHILD:{
//				Widgets.get(PRAYERS_WIDGET,CURSE_ICONS_WIDGET).getChild(MAGIC_DEFLECT_WIDGET_CHILD).click(true);
//				break;
//			}
//			case MELEE_PRAYER_WIDGET_CHILD:{
//				Widgets.get(PRAYERS_WIDGET,CURSE_ICONS_WIDGET).getChild(MAGIC_DEFLECT_WIDGET_CHILD).click(true);
//				break;
//			}
//			}
//		}
//		sleep(500);
//		Tabs.INVENTORY.open();
//	}

	private void moveMouseNear(int x, int y) {// 280,250
		Mouse.move(x + Random.nextInt(-20, 20),y + Random.nextInt(-40, 40), 0, 0);
	}
	
	private boolean checkRun(){
		int energy = Integer.parseInt(Widgets.get(750).getChild(6).getText());
		if(energy > 40 && !Walking.isRunEnabled()){
			Walking.setRun(true);
			sleep(1000);
			return true;
		}
		return false;
	}
	
	//thanks coma/cakemix for method
	private static int getPrice(int id) throws IOException {
		URL url = new URL("http://open.tip.it/json/ge_single_item?item=" + id);
		URLConnection con = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String line = "";
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			line += inputLine;
		}
		in.close();
		if (!line.contains("mark_price"))
			return -1;
		line = line.substring(line.indexOf("mark_price\":\"")
				+ "mark_price\":\"".length());
		line = line.substring(0, line.indexOf("\""));
		return Integer.parseInt(line.replaceAll(",", ""));
	}

	private void traverseSwamp() {
		// TODO: stop clicking on boat ride
		if (swampState == null)
			swampState = (fromBankChoice == FromBankChoices.BAR_CELLAR) ?
					SwampTraverse.TO_TRAP_DOOR : SwampTraverse.TO_SWAMP_GATE;
		switch (swampState) {
		case TO_TRAP_DOOR: {
			log.info("to trap door");
			if (Players.getLocal().getLocation().getY() > 9000) {// ITS OVER
																	// 9000
				swampState = SwampTraverse.TO_SECRET_WALL;
				break;
			}
			SceneObject trapdoor = SceneEntities
					.getNearest(SwampTraverse.TRAP_DOOR_ID);
			if (trapdoor != null) {
				if (trapdoor.isOnScreen() && trapdoor.interact("Open"))
					sleep(2000);
				else if (Walking.walk(SwampTraverse.TRAP_DOOR_TILE)) {
					Camera.turnTo(trapdoor);
					sleep(2000);
				}
			}
			break;
		}
		case TO_SECRET_WALL: {
			log.info("to secret wall");
			if (betweenY(9737, 9837)) {
				swampState = SwampTraverse.TO_WOODEN_DOOR;
				break;
			}
			SceneObject secretWall = SceneEntities
					.getNearest(SwampTraverse.SECRET_WALL_ID);
			if (secretWall != null){
				if (secretWall.isOnScreen() && secretWall.interact("Search"))
					sleep(1000);
				else{
					Walking.walk(secretWall);
					Camera.turnTo(secretWall);
					sleep(500);
				}
			}
			break;
		}
		case TO_WOODEN_DOOR: {
			log.info("to wooden door");
			if (betweenY(0, 9000)) {
				swampState = SwampTraverse.TO_ROPE_BRIDGE;
				break;
			}
			SceneObject woodenDoor = SceneEntities
					.getNearest(SwampTraverse.WOODEN_DOOR_ID);
			if (woodenDoor != null) {
				if (woodenDoor.isOnScreen() && woodenDoor.interact("Open"))
					sleep(3000);
				else if (stepPath(SwampTraverse.CELLAR_PATH)) {
					Camera.turnTo(woodenDoor);
					sleep(2000);
				}
			}
			break;
		}
		case TO_ROPE_BRIDGE: {
			log.info("to rope bridge");
			if (betweenY(3422, 3430)) {
				log.info("player y:" + Players.getLocal().getLocation().getY()
						+ "  between (0,3430)");
				swampState = SwampTraverse.TO_BOAT_CELLAR;
				break;
			}
			SceneObject tree = SceneEntities
					.getNearest(SwampTraverse.ROPE_BRIDGE_TREE_ID);
			if (tree != null) {
				if (tree.isOnScreen() && tree.interact("Climb"))
					sleep(3000);
				else if (Walking.walk(tree)) {
					Camera.turnTo(tree);
					sleep(2000);
				}
			}
			break;
		}
		case TO_BOAT_CELLAR: {
			log.info("to boat (from cellar)");
			if (betweenY(0, 3300)) {
				swampState = SwampTraverse.TO_BARROWS;
				Walking.walk(SwampTraverse.BOAT_TO_BARROWS[SwampTraverse.BOAT_TO_BARROWS.length-1]);
				break;
			}
			SceneObject boat = SceneEntities.getNearest(SwampTraverse.BOAT_ID);
			if (boat != null) {
				if (boat.isOnScreen() && boat.interact("Board"))
					sleep(3000);
				else if (stepPath(SwampTraverse.ROPE_TO_BOAT_PATH)) {
					Camera.turnTo(boat);
					sleep(2000);
				}
			} else if (Walking.walk(SwampTraverse.BOAT_TILE))
				sleep(2000);
			break;
		}
		case TO_SWAMP_GATE: {
			log.info("to swamp gate");
			if(SwampTraverse.SWAMP_GATE_AREA.contains(Players.getLocal())){
				swampState = SwampTraverse.TO_BOAT_SWAMP;
				break;
			}
			SceneObject gate = SceneEntities.getNearest(SwampTraverse.SWAMP_GATE_ID);
			if(gate != null){
				if(gate.isOnScreen() && gate.interact("Open")){
					sleep(2000);
					break;
				}
			}
			if(stepPath(SwampTraverse.BANK_TO_SWAMP_GATE))
				sleep(2000);
			break;
		}
		case TO_BOAT_SWAMP: {
			log.info("to boat (from swamp)");
			if(betweenY(0, 3300)){
				swampState = SwampTraverse.TO_BARROWS;
				Walking.walk(SwampTraverse.BOAT_TO_BARROWS[SwampTraverse.BOAT_TO_BARROWS.length-1]);
				break;
			}
			SceneObject boat = SceneEntities.getNearest(SwampTraverse.BOAT_ID);
			if(boat != null){
				if(boat.isOnScreen() && boat.interact("Board"))
					sleep(3000);
				else 
					Camera.turnTo(boat);
			}
			if(stepPath(SwampTraverse.GATE_TO_BOAT))
				sleep(2000);
			break;
		}
		case TO_BARROWS: {
			log.info("to barrows");
			if (SwampTraverse.BARROWS_AREA.contains(Players.getLocal())) {
				fromBank = banking = false;
				swampState = null; // don't reset barrows, need to remembered
									// where we were from last time, ho
			}
			if (stepPath(SwampTraverse.BOAT_TO_BARROWS))
				sleep(2500);
			if(Inventory.getCount(ROTTON_FOOD_ID) > 0 && Inventory.getItem(ROTTON_FOOD_ID).getWidgetChild().interact("drop"))
				sleep(1000);
			break;
		}
		}
	}
	
	private void traverseHouse(){
		if(houseState == null)
			houseState = HouseTraverse.TO_HOUSE;
		switch(houseState){
		case TO_HOUSE:{
			log.info("to house");
			Camera.setPitch(15);
			if(HouseTraverse.getPortal() != null){
				houseState = (HouseTraverse.getAltar() != null) ?
						HouseTraverse.TO_HOUSE_ALTER : HouseTraverse.TO_CANIFIS_BAR;
				break;
			}
			Item tab = Inventory.getItem(HouseTraverse.HOUSE_TAB_ID);
			if(tab != null){
				if(tab.getWidgetChild().click(true))
					sleep(4000);
			}
			break;
		}
		case TO_HOUSE_ALTER:{
			log.info("to altar");
			if(getPrayerPoints() > (Skills.getRealLevel(Skills.PRAYER) * 9)
					&& HouseTraverse.getAltar() != null){
				houseState = HouseTraverse.TO_CANIFIS_BAR;
				break;
			}
			SceneObject door = HouseTraverse.getAltarDoor();
			if(door != null){
				log.info("open altar door");
				if(door.isOnScreen() && door.interact("Open")){
					sleep(2000);
					break;
				}
				else{ 
					Camera.turnTo(door);
					break;
				}
			}
			SceneObject altar = HouseTraverse.getAltar();
			if(altar != null){
				log.info("pray at altar");
				if(altar.isOnScreen() && altar.interact("Pray"))
					sleep(3000);
				else {
					Camera.turnTo(altar);
					Walking.walk(altar);
				}
			}
			break;
		}
		case TO_CANIFIS_BAR:{
			log.info("to canifis bar");
			if(HouseTraverse.BAR_AREA.contains(Players.getLocal())){
				houseState = HouseTraverse.TO_BANK;
				break;
			}
			SceneObject door = HouseTraverse.getPortalDoor();
			if(door != null){
				log.info("open portal door");
				if(door.isOnScreen() && door.interact("Open")){
					sleep(2000);
					break;
				}
				else {
					Camera.turnTo(door);
					break;
				}
			}
			SceneObject portal = HouseTraverse.getPortal();
			if(portal != null){
				log.info("go to portal");
				if(portal.isOnScreen() && portal.interact("Enter"))
					sleep(4000);
				else {
					Camera.turnTo(portal);
					Walking.walk(portal);
				}
			}
			break;
		}
		case TO_BANK:{
			log.info("to bank");
			if(BANK_AREA.contains(Players.getLocal())){
				toBank = false;
				houseState = null;
				doBank = true;
				break;
			}
			if(Walking.walk(BANK_TILE))
				sleep(2000);
			break;
		}
		}
	}
	
	private void traverseEcto(){
		if(ectoState == null)
			ectoState = EctoTraverse.TO_ECTOFUNGUS;
		switch(ectoState){
		case TO_ECTOFUNGUS:{
			if(EctoTraverse.ECTOFUNGUS_AREA.contains(Players.getLocal())){
				sleep(3500);//wait for refill vial
				ectoState = EctoTraverse.TO_BANK;
				break;
			}
		}
		Item vial = Inventory.getItem(EctoTraverse.ECTOPHIAL_ID);
		if(vial != null){
			if(vial.getWidgetChild().click(true))
				sleep(8000);
			break;
		}
		case TO_BANK:{
			if(BANK_AREA.contains(Players.getLocal())){
				toBank = false;
				ectoState = null;
				doBank = true;
				break;
			}
			if(stepPath(EctoTraverse.ECTOFUNGUS_TO_BANK))
				sleep(2000);
			break;
		}
		}
	}
	
	private void ancientsTraverse(){
		log.info("ancients teleport traverse");
		if(ancientsState == null)
			ancientsState = AncientsTraverse.TO_BAR;
		switch(ancientsState){
		case TO_BAR:{
			log.info("teleporting");
			if(AncientsTraverse.BAR_AREA.contains(Players.getLocal())){
				ancientsState = AncientsTraverse.TO_BANK;
				break;
			}
			AttackStyles.AncientsSpells.KHRYLL_TELEPORT.setCast();
			sleep(4000);
			break;
		}
		case TO_BANK:{
			log.info("walking to bank");
			if(BANK_AREA.contains(Players.getLocal())){
				toBank = false;
				ancientsState = null;
				doBank = true;
				break;
			}
			if(Walking.walk(BANK_TILE))
				sleep(2000);
			break;
		}
		}
	}

	@Override
	public void onRepaint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setFont(new Font("Times New Roman",Font.PLAIN,12));
		g.setColor(Color.RED);
		paintMouse(g);
		//g.drawString("x:" + Mouse.getX() + "y:" + Mouse.getY(), 420, 220);
		if (todraw != null && !banking) {
			todraw.getModel().draw(g);
			for (Polygon p : todraw.getBounds())
				g.drawPolygon(p);
		}
		if(error != null){
			//g.drawImage(errorMessage, 250, 200, null);
			g.setFont(new Font("Times New Roman",Font.BOLD,32));
			g.drawString(error, 315, 260);
			g.setFont(new Font("Times New Roman",Font.PLAIN,12));
		}
		drawStatistics(g);
		//g.fillRect(220, 380, 60, 30);
		
	}

	private void drawStatistics(Graphics2D g){
		formatTime();
		int formattedProfit = 0;
		if((System.currentTimeMillis() - startTime) != 0){
			numRunsHr = (int)Math.round(((numRuns) * 3600000D / (System.currentTimeMillis() - startTime)));
			profitHr = (int) ((profit) * 3600000D / (System.currentTimeMillis() - startTime));
		}
		formattedProfit = profit / 1000;
		profitHr /= 1000;
		g.setColor(Color.BLACK);
		g.setComposite(makeComposite(.5f));
		g.fillRoundRect(370, 55, 145, 200,20,20);
		g.setColor(Color.white);
		g.drawString("Rounds: "+numRuns+"("+numRunsHr+"/h)", 380, 80);
		g.drawString("Rounds this trip: "+numTripRuns, 380, 100);
		g.drawString("Profit:"+formattedProfit+"k("+profitHr+"k/h)", 380, 120);
		g.drawString("Barrows Items: "+numBarrowsItems, 380, 140);
		g.drawString("Runtime: " + hours +":"+ minutes + ":" + seconds, 380, 220);
	}
	
	private void formatTime(){
		millis = System.currentTimeMillis() - startTime;
		hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		seconds = millis / 1000;
	}
	
	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}

	private void drawPathSection(CryptDoor enter, Graphics2D g) {
		if (enter == NW_N_DOOR || enter == NE_N_DOOR)// north side passage
			g.drawImage(northSidePath, 300, 345, null);
		if (enter == NW_W_DOOR || enter == SW_W_DOOR)// west side passage
			g.drawImage(westSidePath, 300, 345, null);
		if (enter == SW_S_DOOR || enter == SE_S_DOOR)// south side passage
			g.drawImage(southSidePath, 300, 437, null);
		if (enter == NE_E_DOOR || enter == SE_E_DOOR)// east side passage
			g.drawImage(eastSidePath, 403, 345, null);
		if (enter == NW_S_DOOR || enter == W_N_DOOR)
			drawVerticalEdge(g, PaintPoint.NW.getPoint());
		if (enter == W_E_DOOR || enter == C_W_DOOR)
			drawHorizontalEdge(g, PaintPoint.W.getPoint());
		if (enter == SW_N_DOOR || enter == W_S_DOOR)
			drawVerticalEdge(g, PaintPoint.W.getPoint());
		if (enter == SW_E_DOOR || enter == S_W_DOOR)
			drawHorizontalEdge(g, PaintPoint.SW.getPoint());
		if (enter == S_N_DOOR || enter == C_S_DOOR)
			drawVerticalEdge(g, PaintPoint.C.getPoint());
		if (enter == S_E_DOOR || enter == SE_W_DOOR)
			drawHorizontalEdge(g, PaintPoint.S.getPoint());
		if (enter == SE_N_DOOR || enter == E_S_DOOR)
			drawVerticalEdge(g, PaintPoint.E.getPoint());
		if (enter == E_W_DOOR || enter == C_E_DOOR)
			drawHorizontalEdge(g, PaintPoint.C.getPoint());
		if (enter == E_N_DOOR || enter == NE_S_DOOR)
			drawVerticalEdge(g, PaintPoint.NE.getPoint());
		if (enter == N_E_DOOR || enter == NE_W_DOOR)
			drawHorizontalEdge(g, PaintPoint.N.getPoint());
		if (enter == N_S_DOOR || enter == C_N_DOOR)
			drawVerticalEdge(g, PaintPoint.N.getPoint());
		if (enter == N_W_DOOR || enter == NW_E_DOOR)
			drawHorizontalEdge(g, PaintPoint.NW.getPoint());
	}

	private void drawVerticalEdge(Graphics2D g, Point p) {
		g.fillRect(p.x + 3, p.y, 3, 37);
	}

	private void drawHorizontalEdge(Graphics2D g, Point p) {
		g.fillRect(p.x, p.y, 37, 3);
	}

	private void paintMouse(Graphics2D g2d) {
		int mx = Mouse.getX();
		int my = Mouse.getY();
		g2d.drawOval(mx - 5, my - 5, 10, 10);
		g2d.drawOval(mx - 3, my - 3, 6, 6);
		g2d.drawLine(mx, my - 5, mx, my + 5);
		g2d.drawLine(mx - 5, my, mx + 5, my);
	}

	@Override
	public void messageReceived(MessageEvent e) {
		if (e.getMessage().contains("wrong"))
			misclickPuzzle = true;
		if (e.getMessage().contains("mechanism grind open"))
			solvedPuzzle = true;
	}


	public void writeFile(String s) {
		File file = new File("tilepath.txt");	
		try {
			if(!file.exists())
				file.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.write(s);
			out.write(System.getProperty("line.separator"));
			out.close();
			log.info("Write Success:" + s);
		} catch (IOException e1) {
			log.info(e1.toString());
		}
	}
	
	public String readFile(){
		String result = "";
		File file = new File("tilepath.txt");
		if(!file.exists())
			log.info("file does not exist");
		try {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		while((line = in.readLine()) != null)
			result+=line;
		in.close();
		} catch (IOException e) {
			log.info(e.toString());
		}
		return result;
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {// g2d.fillRect(220, 310, 60, 30);
//		if (e.getX() >= 220 && e.getX() <= 280 && e.getY() >= 380
//				&& e.getY() <= 410) {
//			Tile t = Players.getLocal().getLocation();
//			writeFile("new Tile(" + t.getX() + "," + t.getY() + ","
//					+ t.getPlane() + "),");
//		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	public class MiscBarrowsGUI extends JFrame{

		private static final long serialVersionUID = 1L;
		public MiscBarrowsGUI(File f){
			_f = f;
			setSize(450,430);
			setTitle("MiscBarrows");
			setResizable(false);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			createContents();
			setVisible(true);
		}
		
		JLabel prayQtyLabel;
		JLabel foodIdLabel;
		JLabel foodQtyLabel;
		JLabel toBankLabel;
		JLabel fromBankLabel;
		JLabel weaponLabel;
		JLabel shieldLabel;
		JLabel ammoLabel;
		JLabel helmLabel;
//		JLabel new JLabel("Chest",SwingConstants.CENTER);
//		JLabel new JLabel("Legs",SwingConstants.CENTER);
//		JLabel new JLabel("Gloves",SwingConstants.CENTER);
//		JLabel new JLabel("Boots",SwingConstants.CENTER);
//		JLabel new JLabel("Necklace",SwingConstants.CENTER);
//		JLabel new JLabel("Ring",SwingConstants.CENTER);
//		JLabel new JLabel("Special Attack");
//		JLabel new JLabel("Protection Pray");
		
		JTextField prayQtyField;
		JTextField foodIdField;
		JTextField foodQtyField;
		
		JTextField dharokWeapon;
		JTextField dharokShield;
		JTextField dharokHelmet;
		JTextField dharokChest;
		JTextField dharokLegs;
		JTextField dharokGloves;
		JTextField dharokBoots;
		JTextField dharokNecklace;
		JTextField dharokCape;
		
		JTextField veracWeapon;
		JTextField veracShield;
		JTextField veracHelmet;
		JTextField veracChest;
		JTextField veracLegs;
		JTextField veracGloves;
		JTextField veracBoots;
		JTextField veracNecklace;
		JTextField veracCape;
		
		JTextField toragWeapon;
		JTextField toragShield;
		JTextField toragHelmet;
		JTextField toragChest;
		JTextField toragLegs;
		JTextField toragGloves;
		JTextField toragBoots;
		JTextField toragNecklace;
		JTextField toragCape;
		
		JTextField karilWeapon;
		JTextField karilShield;
		JTextField karilHelmet;
		JTextField karilChest;
		JTextField karilLegs;
		JTextField karilGloves;
		JTextField karilBoots;
		JTextField karilNecklace;
		JTextField karilCape;
		
		JTextField guthanWeapon;
		JTextField guthanShield;
		JTextField guthanHelmet;
		JTextField guthanChest;
		JTextField guthanLegs;
		JTextField guthanGloves;
		JTextField guthanBoots;
		JTextField guthanNecklace;
		JTextField guthanCape;
		
		JTextField ahrimWeapon;
		JTextField ahrimShield;
		JTextField ahrimHelmet;
		JTextField ahrimChest;
		JTextField ahrimLegs;
		JTextField ahrimGloves;
		JTextField ahrimBoots;
		JTextField ahrimNecklace;
		JTextField ahrimCape;
		
		JTextArea dharokTextArea;
		JTextArea veracTextArea;
		JTextArea toragTextArea;
		JTextArea karilTextArea;
		JTextArea guthanTextArea;
		JTextArea ahrimTextArea;
		JTextArea infoTextArea;
		
		JComboBox toBankComboBox;
		JComboBox fromBankComboBox;
		JComboBox dharokAutocast;
		JComboBox veracAutocast;
		JComboBox toragAutocast;
		JComboBox karilAutocast;
		JComboBox guthanAutocast;
		JComboBox ahrimAutocast;
		
		JButton dharokLoadButton;
		JButton dharokClearButton;
		JButton veracLoadButton;
		JButton veracClearButton;
		JButton toragLoadButton;
		JButton toragClearButton;
		JButton karilLoadButton;
		JButton karilClearButton;
		JButton guthanLoadButton;
		JButton guthanClearButton;
		JButton ahrimLoadButton;
		JButton ahrimClearButton;
		JButton loadButton;
		JButton startButton;
		JButton saveButton;
		
		JRadioButton dharokSpecialAttButton;
		JRadioButton dharokProtectionPrayButton;
		JRadioButton veracSpecialAttButton;
		JRadioButton veracProtectionPrayButton;
		JRadioButton toragSpecialAttButton;
		JRadioButton toragProtectionPrayButton;
		JRadioButton karilSpecialAttButton;
		JRadioButton karilProtectionPrayButton;
		JRadioButton guthanSpecialAttButton;
		JRadioButton guthanProtectionPrayButton;
		JRadioButton ahrimSpecialAttButton;
		JRadioButton ahrimProtectionPrayButton;

		JPanel north;
		JPanel northwest;
		JPanel northeast;
		JPanel center;
		JPanel south;
		JPanel dharokPane;
		JPanel dharokWest;
		JPanel dharokCenter;
		JPanel veracPane;
		JPanel veracWest;
		JPanel veracCenter;
		JPanel toragPane;
		JPanel toragWest;
		JPanel toragCenter;
		JPanel karilPane;
		JPanel karilWest;
		JPanel karilCenter;
		JPanel guthanPane;
		JPanel guthanWest;
		JPanel guthanCenter;
		JPanel ahrimPane;
		JPanel ahrimWest;
		JPanel ahrimCenter;
		JPanel infoPane;
		
		JTabbedPane brotherPanes;
		
		Properties _p;
		File _f;
		
		private void createContents(){
			initComponents();
			setLayout(new BorderLayout());
			north.setSize(new Dimension(400,100));
			north.setLayout(new GridLayout(1,2));
			northwest.setSize(new Dimension(160,100));
			northwest.setLayout(new GridLayout(3,2));
			northeast.setSize(new Dimension(240,100));
			center.setSize(new Dimension(400,290));
			south.setSize(new Dimension(400,40));

			northwest.add(prayQtyLabel);
			northwest.add(prayQtyField);
			northwest.add(foodIdLabel);
			northwest.add(foodIdField);
			northwest.add(foodQtyLabel);
			northwest.add(foodQtyField);
			northeast.add(toBankLabel);
			northeast.add(toBankComboBox);
			northeast.add(fromBankLabel);
			northeast.add(fromBankComboBox);
			
			brotherPanes.setTabPlacement(JTabbedPane.TOP);
			
			dharokPane.setSize(new Dimension(450,240));
			dharokPane.setLayout(new BorderLayout());
			veracPane.setPreferredSize(new Dimension(450,240));
			veracPane.setLayout(new BorderLayout());
			toragPane.setPreferredSize(new Dimension(450,240));
			toragPane.setLayout(new BorderLayout());
			karilPane.setPreferredSize(new Dimension(450,240));
			karilPane.setLayout(new BorderLayout());
			guthanPane.setPreferredSize(new Dimension(450,240));
			guthanPane.setLayout(new BorderLayout());
			ahrimPane.setPreferredSize(new Dimension(450,240));
			ahrimPane.setLayout(new BorderLayout());
			
			dharokWest.setSize(new Dimension(100,240));
			dharokWest.setLayout(new GridLayout(14,4));
			dharokWest.add(new JLabel("Weapon:",SwingConstants.CENTER));
			dharokWest.add(dharokWeapon);
			dharokWest.add(new JLabel("Shield:",SwingConstants.CENTER));
			dharokWest.add(dharokShield);
			dharokWest.add(new JLabel("Helmet:",SwingConstants.CENTER));
			dharokWest.add(dharokHelmet);
			dharokWest.add(new JLabel("Chest:",SwingConstants.CENTER));
			dharokWest.add(dharokChest);
			dharokWest.add(new JLabel("Legs:",SwingConstants.CENTER));
			dharokWest.add(dharokLegs);
			dharokWest.add(new JLabel("Gloves:",SwingConstants.CENTER));
			dharokWest.add(dharokGloves);
			dharokWest.add(new JLabel("Boots:",SwingConstants.CENTER));
			dharokWest.add(dharokBoots);
			dharokWest.add(new JLabel("Necklace:",SwingConstants.CENTER));
			dharokWest.add(dharokNecklace);
			dharokWest.add(new JLabel("Cape:",SwingConstants.CENTER));
			dharokWest.add(dharokCape);
			
			dharokCenter.setPreferredSize(new Dimension(120,240));
			dharokCenter.setLayout(new FlowLayout());
			dharokTextArea.setPreferredSize(new Dimension(300,170));
			dharokTextArea.setBackground(Color.LIGHT_GRAY);
			dharokTextArea.setLineWrap(true);
			dharokTextArea.setWrapStyleWord(true);
			dharokTextArea.setEditable(false);
			dharokLoadButton.setPreferredSize(new Dimension(95,25));
			dharokClearButton.setPreferredSize(new Dimension(95,25));
			dharokCenter.add(dharokLoadButton);
			dharokCenter.add(dharokClearButton);
			dharokCenter.add(dharokTextArea);
			//dharokCenter.add(new JLabel("Special Attack"));
			//dharokCenter.add(dharokSpecialAttButton);
			dharokCenter.add(new JLabel("Protection Pray"));
			dharokProtectionPrayButton.setSelected(true);
			dharokCenter.add(dharokProtectionPrayButton);
//			dharokCenter.add(new JLabel("Autocast spell:"));
//			dharokCenter.add(dharokAutocast);
			
			veracWest.setSize(new Dimension(100,240));
			veracWest.setLayout(new GridLayout(14,2));
			veracWest.add(new JLabel("Weapon:",SwingConstants.CENTER));
			veracWest.add(veracWeapon);
			veracWest.add(new JLabel("Shield:",SwingConstants.CENTER));
			veracWest.add(veracShield);
			veracWest.add(new JLabel("Helmet:",SwingConstants.CENTER));
			veracWest.add(veracHelmet);
			veracWest.add(new JLabel("Chest:",SwingConstants.CENTER));
			veracWest.add(veracChest);
			veracWest.add(new JLabel("Legs:",SwingConstants.CENTER));
			veracWest.add(veracLegs);
			veracWest.add(new JLabel("Gloves:",SwingConstants.CENTER));
			veracWest.add(veracGloves);
			veracWest.add(new JLabel("Boots:",SwingConstants.CENTER));
			veracWest.add(veracBoots);
			veracWest.add(new JLabel("Necklace:",SwingConstants.CENTER));
			veracWest.add(veracNecklace);
			veracWest.add(new JLabel("Cape:",SwingConstants.CENTER));
			veracWest.add(veracCape);
			
			veracCenter.setPreferredSize(new Dimension(120,240));
			veracCenter.setLayout(new FlowLayout());
			veracTextArea.setPreferredSize(new Dimension(300,170));
			veracTextArea.setBackground(Color.LIGHT_GRAY);
			veracTextArea.setLineWrap(true);
			veracTextArea.setWrapStyleWord(true);
			veracTextArea.setEditable(false);
			veracLoadButton.setPreferredSize(new Dimension(95,25));
			veracClearButton.setPreferredSize(new Dimension(95,25));
			veracCenter.add(veracLoadButton);
			veracCenter.add(veracClearButton);
			veracCenter.add(veracTextArea);
			//veracCenter.add(new JLabel("Special Attack"));
			//veracCenter.add(veracSpecialAttButton);
			veracCenter.add(new JLabel("Protection Pray"));
			veracProtectionPrayButton.setSelected(true);
			veracCenter.add(veracProtectionPrayButton);
//			veracCenter.add(new JLabel("Autocast spell:"));
//			veracCenter.add(veracAutocast);
			
			toragWest.setSize(new Dimension(100,240));
			toragWest.setLayout(new GridLayout(14,2));
			toragWest.add(new JLabel("Weapon:",SwingConstants.CENTER));
			toragWest.add(toragWeapon);
			toragWest.add(new JLabel("Shield:",SwingConstants.CENTER));
			toragWest.add(toragShield);
			toragWest.add(new JLabel("Helmet:",SwingConstants.CENTER));
			toragWest.add(toragHelmet);
			toragWest.add(new JLabel("Chest:",SwingConstants.CENTER));
			toragWest.add(toragChest);
			toragWest.add(new JLabel("Legs:",SwingConstants.CENTER));
			toragWest.add(toragLegs);
			toragWest.add(new JLabel("Gloves:",SwingConstants.CENTER));
			toragWest.add(toragGloves);
			toragWest.add(new JLabel("Boots:",SwingConstants.CENTER));
			toragWest.add(toragBoots);
			toragWest.add(new JLabel("Necklace:",SwingConstants.CENTER));
			toragWest.add(toragNecklace);
			toragWest.add(new JLabel("Cape:",SwingConstants.CENTER));
			toragWest.add(toragCape);
			
			toragCenter.setPreferredSize(new Dimension(120,240));
			toragCenter.setLayout(new FlowLayout());
			toragTextArea.setPreferredSize(new Dimension(300,170));
			toragTextArea.setBackground(Color.LIGHT_GRAY);
			toragTextArea.setLineWrap(true);
			toragTextArea.setWrapStyleWord(true);
			toragTextArea.setEditable(false);
			toragLoadButton.setPreferredSize(new Dimension(95,25));
			toragClearButton.setPreferredSize(new Dimension(95,25));
			toragCenter.add(toragLoadButton);
			toragCenter.add(toragClearButton);
			toragCenter.add(toragTextArea);
			//toragCenter.add(new JLabel("Special Attack"));
			//toragCenter.add(toragSpecialAttButton);
			toragCenter.add(new JLabel("Protection Pray"));
			toragProtectionPrayButton.setSelected(true);
			toragCenter.add(toragProtectionPrayButton);
//			toragCenter.add(new JLabel("Autocast spell:"));
//			toragCenter.add(toragAutocast);
			
			karilWest.setSize(new Dimension(100,240));
			karilWest.setLayout(new GridLayout(14,2));
			karilWest.add(new JLabel("Weapon:",SwingConstants.CENTER));
			karilWest.add(karilWeapon);
			karilWest.add(new JLabel("Shield:",SwingConstants.CENTER));
			karilWest.add(karilShield);
			karilWest.add(new JLabel("Helmet:",SwingConstants.CENTER));
			karilWest.add(karilHelmet);
			karilWest.add(new JLabel("Chest:",SwingConstants.CENTER));
			karilWest.add(karilChest);
			karilWest.add(new JLabel("Legs:",SwingConstants.CENTER));
			karilWest.add(karilLegs);
			karilWest.add(new JLabel("Gloves:",SwingConstants.CENTER));
			karilWest.add(karilGloves);
			karilWest.add(new JLabel("Boots:",SwingConstants.CENTER));
			karilWest.add(karilBoots);
			karilWest.add(new JLabel("Necklace:",SwingConstants.CENTER));
			karilWest.add(karilNecklace);
			karilWest.add(new JLabel("Cape:",SwingConstants.CENTER));
			karilWest.add(karilCape);
			
			karilCenter.setPreferredSize(new Dimension(120,240));
			karilCenter.setLayout(new FlowLayout());
			karilTextArea.setPreferredSize(new Dimension(300,170));
			karilTextArea.setBackground(Color.LIGHT_GRAY);
			karilTextArea.setLineWrap(true);
			karilTextArea.setWrapStyleWord(true);
			karilTextArea.setEditable(false);
			karilLoadButton.setPreferredSize(new Dimension(95,25));
			karilClearButton.setPreferredSize(new Dimension(95,25));
			karilCenter.add(karilLoadButton);
			karilCenter.add(karilClearButton);
			karilCenter.add(karilTextArea);
			//karilCenter.add(new JLabel("Special Attack"));
			//karilCenter.add(karilSpecialAttButton);
			karilCenter.add(new JLabel("Protection Pray"));
			karilProtectionPrayButton.setSelected(true);
			karilCenter.add(karilProtectionPrayButton);
//			karilCenter.add(new JLabel("Autocast spell:"));
//			karilCenter.add(karilAutocast);
			
			guthanWest.setSize(new Dimension(100,240));
			guthanWest.setLayout(new GridLayout(14,2));
			guthanWest.add(new JLabel("Weapon:",SwingConstants.CENTER));
			guthanWest.add(guthanWeapon);
			guthanWest.add(new JLabel("Shield:",SwingConstants.CENTER));
			guthanWest.add(guthanShield);
			guthanWest.add(new JLabel("Helmet:",SwingConstants.CENTER));
			guthanWest.add(guthanHelmet);
			guthanWest.add(new JLabel("Chest:",SwingConstants.CENTER));
			guthanWest.add(guthanChest);
			guthanWest.add(new JLabel("Legs:",SwingConstants.CENTER));
			guthanWest.add(guthanLegs);
			guthanWest.add(new JLabel("Gloves:",SwingConstants.CENTER));
			guthanWest.add(guthanGloves);
			guthanWest.add(new JLabel("Boots:",SwingConstants.CENTER));
			guthanWest.add(guthanBoots);
			guthanWest.add(new JLabel("Necklace:",SwingConstants.CENTER));
			guthanWest.add(guthanNecklace);
			guthanWest.add(new JLabel("Cape:",SwingConstants.CENTER));
			guthanWest.add(guthanCape);
			
			guthanCenter.setPreferredSize(new Dimension(120,240));
			guthanCenter.setLayout(new FlowLayout());
			guthanTextArea.setPreferredSize(new Dimension(300,170));
			guthanTextArea.setBackground(Color.LIGHT_GRAY);
			guthanTextArea.setLineWrap(true);
			guthanTextArea.setWrapStyleWord(true);
			guthanTextArea.setEditable(false);
			guthanLoadButton.setPreferredSize(new Dimension(95,25));
			guthanClearButton.setPreferredSize(new Dimension(95,25));
			guthanCenter.add(guthanLoadButton);
			guthanCenter.add(guthanClearButton);
			guthanCenter.add(guthanTextArea);
			//guthanCenter.add(new JLabel("Special Attack"));
			//guthanCenter.add(guthanSpecialAttButton);
			guthanCenter.add(new JLabel("Protection Pray"));
			guthanProtectionPrayButton.setSelected(true);
			guthanCenter.add(guthanProtectionPrayButton);
//			guthanCenter.add(new JLabel("Autocast spell:"));
//			guthanCenter.add(guthanAutocast);
			
			ahrimWest.setSize(new Dimension(100,240));
			ahrimWest.setLayout(new GridLayout(14,2));
			ahrimWest.add(new JLabel("Weapon:",SwingConstants.CENTER));
			ahrimWest.add(ahrimWeapon);
			ahrimWest.add(new JLabel("Shield:",SwingConstants.CENTER));
			ahrimWest.add(ahrimShield);
			ahrimWest.add(new JLabel("Helmet:",SwingConstants.CENTER));
			ahrimWest.add(ahrimHelmet);
			ahrimWest.add(new JLabel("Chest:",SwingConstants.CENTER));
			ahrimWest.add(ahrimChest);
			ahrimWest.add(new JLabel("Legs:",SwingConstants.CENTER));
			ahrimWest.add(ahrimLegs);
			ahrimWest.add(new JLabel("Gloves:",SwingConstants.CENTER));
			ahrimWest.add(ahrimGloves);
			ahrimWest.add(new JLabel("Boots:",SwingConstants.CENTER));
			ahrimWest.add(ahrimBoots);
			ahrimWest.add(new JLabel("Necklace:",SwingConstants.CENTER));
			ahrimWest.add(ahrimNecklace);
			ahrimWest.add(new JLabel("Cape:",SwingConstants.CENTER));
			ahrimWest.add(ahrimCape);
			
			ahrimCenter.setPreferredSize(new Dimension(120,240));
			ahrimCenter.setLayout(new FlowLayout());
			ahrimTextArea.setPreferredSize(new Dimension(300,170));
			ahrimTextArea.setBackground(Color.LIGHT_GRAY);
			ahrimTextArea.setLineWrap(true);
			ahrimTextArea.setWrapStyleWord(true);
			ahrimTextArea.setEditable(false);
			ahrimLoadButton.setPreferredSize(new Dimension(95,25));
			ahrimClearButton.setPreferredSize(new Dimension(95,25));
			ahrimCenter.add(ahrimLoadButton);
			ahrimCenter.add(ahrimClearButton);
			ahrimCenter.add(ahrimTextArea);
			//ahrimCenter.add(new JLabel("Special Attack"));
			//ahrimCenter.add(ahrimSpecialAttButton);
			ahrimCenter.add(new JLabel("Protection Pray"));
			ahrimProtectionPrayButton.setSelected(true);
			ahrimCenter.add(ahrimProtectionPrayButton);
//			ahrimCenter.add(new JLabel("Autocast spell:"));
//			ahrimCenter.add(ahrimAutocast);
			
			brotherPanes.addTab("Dharok", dharokPane);
			dharokPane.add(dharokWest,BorderLayout.WEST);
			dharokPane.add(dharokCenter,BorderLayout.CENTER);
			
			brotherPanes.addTab("Verac", veracPane);
			veracPane.add(veracWest,BorderLayout.WEST);
			veracPane.add(veracCenter,BorderLayout.CENTER);
			
			brotherPanes.addTab("Torag", toragPane);
			toragPane.add(toragWest,BorderLayout.WEST);
			toragPane.add(toragCenter,BorderLayout.CENTER);
			
			brotherPanes.addTab("Karil", karilPane);
			karilPane.add(karilWest,BorderLayout.WEST);
			karilPane.add(karilCenter,BorderLayout.CENTER);
			
			brotherPanes.addTab("Guthan", guthanPane);
			guthanPane.add(guthanWest,BorderLayout.WEST);
			guthanPane.add(guthanCenter,BorderLayout.CENTER);
			
			brotherPanes.addTab("Ahrim", ahrimPane);
			ahrimPane.add(ahrimWest,BorderLayout.WEST);
			ahrimPane.add(ahrimCenter,BorderLayout.CENTER);
			
			ahrimTextArea.setBackground(Color.LIGHT_GRAY);
			ahrimTextArea.setLineWrap(true);
			ahrimTextArea.setWrapStyleWord(true);
			ahrimTextArea.setEditable(false);
			infoPane.add(infoTextArea);
			brotherPanes.add("Info",infoPane);
			
			center.add(brotherPanes);
			
			south.add(saveButton);
			south.add(startButton);
			south.add(loadButton);
			
			north.add(northwest);
			north.add(northeast);
			add(north,BorderLayout.NORTH);
			add(center,BorderLayout.CENTER);
			add(south,BorderLayout.SOUTH);
			
			Listener l = new Listener();
			dharokLoadButton.addActionListener(l);
			dharokClearButton.addActionListener(l);
			veracLoadButton.addActionListener(l);
			veracClearButton.addActionListener(l);
			toragLoadButton.addActionListener(l);
			toragClearButton.addActionListener(l);
			karilLoadButton.addActionListener(l);
			karilClearButton.addActionListener(l);
			guthanLoadButton.addActionListener(l);
			guthanClearButton.addActionListener(l);
			ahrimLoadButton.addActionListener(l);
			ahrimClearButton.addActionListener(l);
			startButton.addActionListener(l);
			loadButton.addActionListener(l);
			saveButton.addActionListener(l);
		}

		private void initComponents(){
			north = new JPanel();
			northwest = new JPanel();
			northeast = new JPanel();
			center = new JPanel();
			south = new JPanel();
			
			prayQtyLabel = new JLabel("# Pray Potion",SwingConstants.CENTER);
			foodIdLabel = new JLabel("Food ID",SwingConstants.CENTER);//change to food selection someday
			foodQtyLabel = new JLabel("# Food",SwingConstants.CENTER);
			toBankLabel = new JLabel("To Bank Path");
			fromBankLabel = new JLabel("To Barrows Path");
			
			prayQtyField = new JTextField(6);
			foodIdField = new JTextField(6);
			foodQtyField = new JTextField(6);
			
			toBankComboBox = new JComboBox(new String[]{"House portal","Ectofungus","Ancients Teleport"});
			fromBankComboBox = new JComboBox(new String[]{"Cellar shortcut","Swamp"});
			
			brotherPanes = new JTabbedPane();
			
			
			
			dharokPane = new JPanel();
			dharokWest = new JPanel();
			dharokCenter = new JPanel();
			dharokWeapon = new JTextField("0",7);
			dharokShield = new JTextField("0",7);
			dharokHelmet = new JTextField("0",7);
			dharokChest = new JTextField("0",7);
			dharokLegs = new JTextField("0",7);
			dharokGloves = new JTextField("0",7);
			dharokBoots = new JTextField("0",7);
			dharokNecklace = new JTextField("0",7);
			dharokCape = new JTextField("0",7);
			dharokLoadButton = new JButton("Load Gear");
			dharokClearButton = new JButton("Clear Gear");
			dharokTextArea = new JTextArea("- Press Load Gear to load currently equipped gear to be worn when fighting this brother. If" +
					" you choose to not load gear, the brother will be fought with whatever it has on at the time.");
			dharokSpecialAttButton = new JRadioButton();
			dharokProtectionPrayButton = new JRadioButton();
			dharokAutocast = new JComboBox(new String[]{"None","Magic Dart","Fire Surge"});
			
			veracPane = new JPanel();
			veracWest = new JPanel();
			veracCenter = new JPanel();
			veracWeapon = new JTextField("0",7);
			veracShield = new JTextField("0",7);
			veracHelmet = new JTextField("0",7);
			veracChest = new JTextField("0",7);
			veracLegs = new JTextField("0",7);
			veracGloves = new JTextField("0",7);
			veracBoots = new JTextField("0",7);
			veracNecklace = new JTextField("0",7);
			veracCape = new JTextField("0",7);
			veracLoadButton = new JButton("Load Gear");
			veracClearButton = new JButton("Clear Gear");
			veracTextArea = new JTextArea("- Press Load Gear to load currently equipped gear to be worn when fighting this brother. If" +
					" you choose to not load gear, the brother will be fought with whatever it has on at the time.");
			veracSpecialAttButton = new JRadioButton();
			veracProtectionPrayButton = new JRadioButton();
			veracAutocast = new JComboBox(new String[]{"None","Magic Dart","Fire Surge"});
			
			toragPane = new JPanel();
			toragWest = new JPanel();
			toragCenter = new JPanel();
			toragWeapon = new JTextField("0",7);
			toragShield = new JTextField("0",7);
			toragHelmet = new JTextField("0",7);
			toragChest = new JTextField("0",7);
			toragLegs = new JTextField("0",7);
			toragGloves = new JTextField("0",7);
			toragBoots = new JTextField("0",7);
			toragNecklace = new JTextField("0",7);
			toragCape = new JTextField("0",7);
			toragLoadButton = new JButton("Load Gear");
			toragClearButton = new JButton("Clear Gear");
			toragTextArea = new JTextArea("- Press Load Gear to load currently equipped gear to be worn when fighting this brother. If" +
					" you choose to not load gear, the brother will be fought with whatever it has on at the time.");
			toragSpecialAttButton = new JRadioButton();
			toragProtectionPrayButton = new JRadioButton();
			toragAutocast = new JComboBox(new String[]{"None","Magic Dart","Fire Surge"});
			
			karilPane = new JPanel();
			karilWest = new JPanel();
			karilCenter = new JPanel();
			karilWeapon = new JTextField("0",7);
			karilShield = new JTextField("0",7);
			karilHelmet = new JTextField("0",7);
			karilChest = new JTextField("0",7);
			karilLegs = new JTextField("0",7);
			karilGloves = new JTextField("0",7);
			karilBoots = new JTextField("0",7);
			karilNecklace = new JTextField("0",7);
			karilCape = new JTextField("0",7);
			karilLoadButton = new JButton("Load Gear");
			karilClearButton = new JButton("Clear Gear");
			karilTextArea = new JTextArea("- Press Load Gear to load currently equipped gear to be worn when fighting this brother. If" +
					" you choose to not load gear, the brother will be fought with whatever it has on at the time.");
			karilSpecialAttButton = new JRadioButton();
			karilProtectionPrayButton = new JRadioButton();
			karilAutocast = new JComboBox(new String[]{"None","Magic Dart","Fire Surge"});
			
			guthanPane = new JPanel();
			guthanWest = new JPanel();
			guthanCenter = new JPanel();
			guthanWeapon = new JTextField("0",7);
			guthanShield = new JTextField("0",7);
			guthanHelmet = new JTextField("0",7);
			guthanChest = new JTextField("0",7);
			guthanLegs = new JTextField("0",7);
			guthanGloves = new JTextField("0",7);
			guthanBoots = new JTextField("0",7);
			guthanNecklace = new JTextField("0",7);
			guthanCape = new JTextField("0",7);
			guthanLoadButton = new JButton("Load Gear");
			guthanClearButton = new JButton("Clear Gear");
			guthanTextArea = new JTextArea("- Press Load Gear to load currently equipped gear to be worn when fighting this brother. If" +
					" you choose to not load gear, the brother will be fought with whatever it has on at the time..");
			guthanSpecialAttButton = new JRadioButton();
			guthanProtectionPrayButton = new JRadioButton();
			guthanAutocast = new JComboBox(new String[]{"None","Magic Dart","Fire Surge"});
			
			ahrimPane = new JPanel();
			ahrimWest = new JPanel();
			ahrimCenter = new JPanel();
			ahrimWeapon = new JTextField("0",7);
			ahrimShield = new JTextField("0",7);
			ahrimHelmet = new JTextField("0",7);
			ahrimChest = new JTextField("0",7);
			ahrimLegs = new JTextField("0",7);
			ahrimGloves = new JTextField("0",7);
			ahrimBoots = new JTextField("0",7);
			ahrimNecklace = new JTextField("0",7);
			ahrimCape = new JTextField("0",7);
			ahrimLoadButton = new JButton("Load Gear");
			ahrimClearButton = new JButton("Clear Gear");
			ahrimTextArea = new JTextArea("- Press Load Gear to load currently equipped gear to be worn when fighting this brother. If" +
					" you choose to not load gear, the brother will be fought with whatever it has on at the time.");
			ahrimSpecialAttButton = new JRadioButton();
			ahrimProtectionPrayButton = new JRadioButton();
			ahrimAutocast = new JComboBox(new String[]{"None","Magic Dart","Fire Surge"});
			
			infoPane = new JPanel();
			infoTextArea = new JTextArea("Dynamic Signatures now available!\nVisist rapidbots.clanteam.com");
			
			saveButton = new JButton("Save");
			startButton = new JButton("Start");
			loadButton = new JButton("Load");
			
		}
		

		private void setupProperties(){
			Properties defaultProps = new Properties();
			defaultProps.setProperty("prayQtyField", "0");
			defaultProps.setProperty("foodIdField", "0");
			defaultProps.setProperty("foodQtyField", "0");
			defaultProps.setProperty("toBankComboBox", "House portal");
			defaultProps.setProperty("fromBankComboBox", "Cellar shortcut");
			FileInputStream in;
			try {
				if(!_f.exists()){
					_f.createNewFile();
				}
				else{
					log.info("file found");
				}
				in = new FileInputStream(_f);
				defaultProps.load(in);
				in.close();
			} catch (FileNotFoundException e) {
				log.info(e.toString());
			} catch (IOException e) {
				log.info(e.toString());
			}
			_p = new Properties(defaultProps);
		}
		
		private void loadProperties(){
			log.info("load settings:");
			FileInputStream in;
			try {
				if(!_f.exists()){
					_f.createNewFile();
				}
				else{
					log.info("file found");
				}
				in = new FileInputStream(_f);
				_p.load(in);
				in.close();
			} catch (FileNotFoundException e) {
				log.info(e.toString());
			} catch (IOException e) {
				log.info(e.toString());
			}
			/*
			 * p.setProperty("prayQtyField", prayQtyField.getText());
			_p.setProperty("foodIdField", foodIdField.getText());
			_p.setProperty("foodQtyField", foodQtyField.getText());
			_p.setProperty("toBankComboBox", toBankComboBox.getSelectedItem().toString());
			_p.setProperty("fromBankComboBox", fromBankComboBox.getSelectedItem().toString());
			 */
			
			prayQtyField.setText(_p.getProperty("prayQtyField","0"));
			foodIdField.setText(_p.getProperty("foodIdField","0"));
			foodQtyField.setText(_p.getProperty("foodQtyField","0"));
			toBankComboBox.setSelectedItem(_p.getProperty("toBankComboBox", "House portal"));
			fromBankComboBox.setSelectedItem(_p.getProperty("fromBankComboBox", "Cellar shortcut"));
			
			dharokProtectionPrayButton.setSelected(Boolean.valueOf(_p.getProperty("dharokProtectionPrayButton", "true")));
			veracProtectionPrayButton.setSelected(Boolean.valueOf(_p.getProperty("veracProtectionPrayButton", "true")));
			toragProtectionPrayButton.setSelected(Boolean.valueOf(_p.getProperty("toragProtectionPrayButton", "true")));
			karilProtectionPrayButton.setSelected(Boolean.valueOf(_p.getProperty("karilProtectionPrayButton", "true")));
			guthanProtectionPrayButton.setSelected(Boolean.valueOf(_p.getProperty("guthanProtectionPrayButton", "true")));
			ahrimProtectionPrayButton.setSelected(Boolean.valueOf(_p.getProperty("ahrimProtectionPrayButton", "true")));
			
			dharokWeapon.setText(_p.getProperty("dharokWeapon","0"));
			dharokShield.setText(_p.getProperty("dharokShield","0"));
			dharokHelmet.setText(_p.getProperty("dharokHelmet","0"));
			dharokChest.setText(_p.getProperty("dharokChest","0"));
			dharokLegs.setText(_p.getProperty("dharokLegs","0"));
			dharokGloves.setText(_p.getProperty("dharokGloves","0"));
			dharokBoots.setText(_p.getProperty("dharokBoots","0"));
			dharokNecklace.setText(_p.getProperty("dharokNecklace","0"));
			dharokCape.setText(_p.getProperty("dharokCape","0"));
			
			veracWeapon.setText(_p.getProperty("veracWeapon","0"));
			veracShield.setText(_p.getProperty("veracShield","0"));
			veracHelmet.setText(_p.getProperty("veracHelmet","0"));
			veracChest.setText(_p.getProperty("veracChest","0"));
			veracLegs.setText(_p.getProperty("veracLegs","0"));
			veracGloves.setText(_p.getProperty("veracGloves","0"));
			veracBoots.setText(_p.getProperty("veracBoots","0"));
			veracNecklace.setText(_p.getProperty("veracNecklace","0"));
			veracCape.setText(_p.getProperty("veracCape","0"));
			
			toragWeapon.setText(_p.getProperty("toragWeapon","0"));
			toragShield.setText(_p.getProperty("toragShield","0"));
			toragHelmet.setText(_p.getProperty("toragHelmet","0"));
			toragChest.setText(_p.getProperty("toragChest","0"));
			toragLegs.setText(_p.getProperty("toragLegs","0"));
			toragGloves.setText(_p.getProperty("toragGloves","0"));
			toragBoots.setText(_p.getProperty("toragBoots","0"));
			toragNecklace.setText(_p.getProperty("toragNecklace","0"));
			toragCape.setText(_p.getProperty("toragCape","0"));
			
			karilWeapon.setText(_p.getProperty("karilWeapon","0"));
			karilShield.setText(_p.getProperty("karilShield","0"));
			karilHelmet.setText(_p.getProperty("karilHelmet","0"));
			karilChest.setText(_p.getProperty("karilChest","0"));
			karilLegs.setText(_p.getProperty("karilLegs","0"));
			karilGloves.setText(_p.getProperty("karilGloves","0"));
			karilBoots.setText(_p.getProperty("karilBoots","0"));
			karilNecklace.setText(_p.getProperty("karilNecklace","0"));
			karilCape.setText(_p.getProperty("karilCape","0"));
			
			guthanWeapon.setText(_p.getProperty("guthanWeapon","0"));
			guthanShield.setText(_p.getProperty("guthanShield","0"));
			guthanHelmet.setText(_p.getProperty("guthanHelmet","0"));
			guthanChest.setText(_p.getProperty("guthanChest","0"));
			guthanLegs.setText(_p.getProperty("guthanLegs","0"));
			guthanGloves.setText(_p.getProperty("guthanGloves","0"));
			guthanBoots.setText(_p.getProperty("guthanBoots","0"));
			guthanNecklace.setText(_p.getProperty("guthanNecklace","0"));
			guthanCape.setText(_p.getProperty("guthanCape","0"));
			
			ahrimWeapon.setText(_p.getProperty("ahrimWeapon","0"));
			ahrimShield.setText(_p.getProperty("ahrimShield","0"));
			ahrimHelmet.setText(_p.getProperty("ahrimHelmet","0"));
			ahrimChest.setText(_p.getProperty("ahrimChest","0"));
			ahrimLegs.setText(_p.getProperty("ahrimLegs","0"));
			ahrimGloves.setText(_p.getProperty("ahrimGloves","0"));
			ahrimBoots.setText(_p.getProperty("ahrimBoots","0"));
			ahrimNecklace.setText(_p.getProperty("ahrimNecklace","0"));
			ahrimCape.setText(_p.getProperty("ahrimCape","0"));
		}
		
		private void saveProperties(){
			//TODO , spec att / prot pray radio buttons
			_p.setProperty("prayQtyField", prayQtyField.getText());
			_p.setProperty("foodIdField", foodIdField.getText());
			_p.setProperty("foodQtyField", foodQtyField.getText());
			_p.setProperty("toBankComboBox", toBankComboBox.getSelectedItem().toString());
			_p.setProperty("fromBankComboBox", fromBankComboBox.getSelectedItem().toString());
			
			_p.setProperty("dharokProtectionPrayButton", ""+dharokProtectionPrayButton.isSelected());
			_p.setProperty("veracProtectionPrayButton", ""+veracProtectionPrayButton.isSelected());
			_p.setProperty("toragProtectionPrayButton", ""+toragProtectionPrayButton.isSelected());
			_p.setProperty("karilProtectionPrayButton", ""+karilProtectionPrayButton.isSelected());
			_p.setProperty("guthanProtectionPrayButton", ""+guthanProtectionPrayButton.isSelected());
			_p.setProperty("ahrimProtectionPrayButton", ""+ahrimProtectionPrayButton.isSelected());
			
			_p.setProperty("dharokWeapon", dharokWeapon.getText());
			_p.setProperty("dharokShield", dharokShield.getText());
			_p.setProperty("dharokHelmet",dharokHelmet.getText());
			_p.setProperty("dharokChest", dharokChest.getText());
			_p.setProperty("dharokLegs", dharokLegs.getText());
			_p.setProperty("dharokGloves", dharokGloves.getText());
			_p.setProperty("dharokBoots", dharokBoots.getText());
			_p.setProperty("dharokNecklace", dharokNecklace.getText());
			_p.setProperty("dharokCape", dharokCape.getText());
			
			_p.setProperty("veracWeapon", veracWeapon.getText());
			_p.setProperty("veracShield", veracShield.getText());
			_p.setProperty("veracHelmet", veracHelmet.getText());
			_p.setProperty("veracChest", veracChest.getText());
			_p.setProperty("veracLegs", veracLegs.getText());
			_p.setProperty("veracGloves", veracGloves.getText());
			_p.setProperty("veracBoots", veracBoots.getText());
			_p.setProperty("veracNecklace", veracNecklace.getText());
			_p.setProperty("veracCape", veracCape.getText());
			
			_p.setProperty("toragWeapon", toragWeapon.getText());
			_p.setProperty("toragShield", toragShield.getText());
			_p.setProperty("toragHelmet", toragHelmet.getText());
			_p.setProperty("toragChest", toragChest.getText());
			_p.setProperty("toragLegs", toragLegs.getText());
			_p.setProperty("toragGloves", toragGloves.getText());
			_p.setProperty("toragBoots", toragBoots.getText());
			_p.setProperty("toragNecklace", toragNecklace.getText());
			_p.setProperty("toragCape", toragCape.getText());
			
			_p.setProperty("karilWeapon", karilWeapon.getText());
			_p.setProperty("karilShield", karilShield.getText());
			_p.setProperty("karilHelmet", karilHelmet.getText());
			_p.setProperty("karilChest", karilChest.getText());
			_p.setProperty("karilLegs", karilLegs.getText());
			_p.setProperty("karilGloves", karilGloves.getText());
			_p.setProperty("karilBoots", karilBoots.getText());
			_p.setProperty("karilNecklace", karilNecklace.getText());
			_p.setProperty("karilCape", karilCape.getText());
			
			_p.setProperty("guthanWeapon", guthanWeapon.getText());
			_p.setProperty("guthanShield", guthanShield.getText());
			_p.setProperty("guthanHelmet", guthanHelmet.getText());
			_p.setProperty("guthanChest", guthanChest.getText());
			_p.setProperty("guthanLegs", guthanLegs.getText());
			_p.setProperty("guthanGloves", guthanGloves.getText());
			_p.setProperty("guthanBoots", guthanBoots.getText());
			_p.setProperty("guthanNecklace", guthanNecklace.getText());
			_p.setProperty("guthanCape", guthanCape.getText());
			
			_p.setProperty("ahrimWeapon", ahrimWeapon.getText());
			_p.setProperty("ahrimShield", ahrimShield.getText());
			_p.setProperty("ahrimHelmet", ahrimHelmet.getText());
			_p.setProperty("ahrimChest", ahrimChest.getText());
			_p.setProperty("ahrimLegs", ahrimLegs.getText());
			_p.setProperty("ahrimGloves", ahrimGloves.getText());
			_p.setProperty("ahrimBoots", ahrimBoots.getText());
			_p.setProperty("ahrimNecklace", ahrimNecklace.getText());
			_p.setProperty("ahrimCape", ahrimCape.getText());
			
			try {
				if(!_f.exists()){
					_f.createNewFile();
				}
				else{
					log.info("file found");
				}
				FileOutputStream out = new FileOutputStream(_f);
				_p.store(out, "saved settings.");
				out.close();
			} catch (FileNotFoundException e) {
				log.info("file not found: "+e);
			} catch (IOException e) {
				log.info("IO exception: "+e);
			}
		}
		
		private class Listener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == dharokLoadButton){
					dharokWeapon.setText(""+Gear.WEAPON.getId());
					dharokShield.setText(""+Gear.SHIELD.getId());
					dharokHelmet.setText(""+Gear.HELMET.getId());
					dharokChest.setText(""+Gear.CHEST.getId());
					dharokLegs.setText(""+Gear.LEGS.getId());
					dharokGloves.setText(""+Gear.GLOVES.getId());
					dharokBoots.setText(""+Gear.BOOTS.getId());
					dharokNecklace.setText(""+Gear.NECKLACE.getId());
					dharokCape.setText(""+Gear.CAPE.getId());
				}
				if(e.getSource() == veracLoadButton){
					veracWeapon.setText(""+Gear.WEAPON.getId());
					veracShield.setText(""+Gear.SHIELD.getId());
					veracHelmet.setText(""+Gear.HELMET.getId());
					veracChest.setText(""+Gear.CHEST.getId());
					veracLegs.setText(""+Gear.LEGS.getId());
					veracGloves.setText(""+Gear.GLOVES.getId());
					veracBoots.setText(""+Gear.BOOTS.getId());
					veracNecklace.setText(""+Gear.NECKLACE.getId());
					veracCape.setText(""+Gear.CAPE.getId());
				}
				if(e.getSource() == toragLoadButton){
					toragWeapon.setText(""+Gear.WEAPON.getId());
					toragShield.setText(""+Gear.SHIELD.getId());
					toragHelmet.setText(""+Gear.HELMET.getId());
					toragChest.setText(""+Gear.CHEST.getId());
					toragLegs.setText(""+Gear.LEGS.getId());
					toragGloves.setText(""+Gear.GLOVES.getId());
					toragBoots.setText(""+Gear.BOOTS.getId());
					toragNecklace.setText(""+Gear.NECKLACE.getId());
					toragCape.setText(""+Gear.CAPE.getId());
				}
				if(e.getSource() == karilLoadButton){
					karilWeapon.setText(""+Gear.WEAPON.getId());
					karilShield.setText(""+Gear.SHIELD.getId());
					karilHelmet.setText(""+Gear.HELMET.getId());
					karilChest.setText(""+Gear.CHEST.getId());
					karilLegs.setText(""+Gear.LEGS.getId());
					karilGloves.setText(""+Gear.GLOVES.getId());
					karilBoots.setText(""+Gear.BOOTS.getId());
					karilNecklace.setText(""+Gear.NECKLACE.getId());
					karilCape.setText(""+Gear.CAPE.getId());
				}
				if(e.getSource() == guthanLoadButton){
					guthanWeapon.setText(""+Gear.WEAPON.getId());
					guthanShield.setText(""+Gear.SHIELD.getId());
					guthanHelmet.setText(""+Gear.HELMET.getId());
					guthanChest.setText(""+Gear.CHEST.getId());
					guthanLegs.setText(""+Gear.LEGS.getId());
					guthanGloves.setText(""+Gear.GLOVES.getId());
					guthanBoots.setText(""+Gear.BOOTS.getId());
					guthanNecklace.setText(""+Gear.NECKLACE.getId());
					guthanCape.setText(""+Gear.CAPE.getId());
				}
				if(e.getSource() == ahrimLoadButton){
					ahrimWeapon.setText(""+Gear.WEAPON.getId());
					ahrimShield.setText(""+Gear.SHIELD.getId());
					ahrimHelmet.setText(""+Gear.HELMET.getId());
					ahrimChest.setText(""+Gear.CHEST.getId());
					ahrimLegs.setText(""+Gear.LEGS.getId());
					ahrimGloves.setText(""+Gear.GLOVES.getId());
					ahrimBoots.setText(""+Gear.BOOTS.getId());
					ahrimNecklace.setText(""+Gear.NECKLACE.getId());
					ahrimCape.setText(""+Gear.CAPE.getId());
				}
				
				if(e.getSource() == dharokClearButton){
					dharokWeapon.setText("0");
					dharokShield.setText("0");
					dharokHelmet.setText("0");
					dharokChest.setText("0");
					dharokLegs.setText("0");
					dharokGloves.setText("0");
					dharokBoots.setText("0");;
					dharokNecklace.setText("0");;
					dharokCape.setText("0");
				}
				if(e.getSource() == veracClearButton){
					veracWeapon.setText("0");
					veracShield.setText("0");
					veracHelmet.setText("0");
					veracChest.setText("0");
					veracLegs.setText("0");
					veracGloves.setText("0");
					veracBoots.setText("0");
					veracNecklace.setText("0");
					veracCape.setText("0");
				}
				if(e.getSource() == toragClearButton){
					toragWeapon.setText("0");
					toragShield.setText("0");
					toragHelmet.setText("0");
					toragChest.setText("0");
					toragLegs.setText("0");
					toragGloves.setText("0");
					toragBoots.setText("0");
					toragNecklace.setText("0");
					toragCape.setText("0");
				}
				if(e.getSource() == karilClearButton){
					karilWeapon.setText("0");
					karilShield.setText("0");
					karilHelmet.setText("0");
					karilChest.setText("0");
					karilLegs.setText("0");
					karilGloves.setText("0");
					karilBoots.setText("0");
					karilNecklace.setText("0");
					karilCape.setText("0");
				}
				if(e.getSource() == guthanClearButton){
					guthanWeapon.setText("0");
					guthanShield.setText("0");
					guthanHelmet.setText("0");
					guthanChest.setText("0");
					guthanLegs.setText("0");
					guthanGloves.setText("0");
					guthanBoots.setText("0");;
					guthanNecklace.setText("0");
					guthanCape.setText("0");
				}
				if(e.getSource() == ahrimClearButton){
					ahrimWeapon.setText("0");
					ahrimShield.setText("0");
					ahrimHelmet.setText("0");
					ahrimChest.setText("0");
					ahrimLegs.setText("0");
					ahrimGloves.setText("0");
					ahrimBoots.setText("0");
					ahrimNecklace.setText("0");
					ahrimCape.setText("0");
				}
				
				if(e.getSource() == saveButton){
					setupProperties();
					saveProperties();
				}
				
				if(e.getSource() == loadButton){
					setupProperties();
					loadProperties();
				}

				if(e.getSource() == startButton){
					log.info("start button pressed");
					ArrayList<Integer> dharokGearChoices = new ArrayList<Integer>();
					ArrayList<Integer> veracGearChoices = new ArrayList<Integer>();
					ArrayList<Integer> toragGearChoices = new ArrayList<Integer>();
					ArrayList<Integer> karilGearChoices = new ArrayList<Integer>();
					ArrayList<Integer> guthanGearChoices = new ArrayList<Integer>();
					ArrayList<Integer> ahrimGearChoices = new ArrayList<Integer>();

					dharokGearChoices.add(Integer.parseInt(dharokWeapon.getText()));
					dharokGearChoices.add(Integer.parseInt(dharokShield.getText()));
					dharokGearChoices.add(Integer.parseInt(dharokHelmet.getText()));
					dharokGearChoices.add(Integer.parseInt(dharokChest.getText()));
					dharokGearChoices.add(Integer.parseInt(dharokLegs.getText()));
					dharokGearChoices.add(Integer.parseInt(dharokGloves.getText()));
					dharokGearChoices.add(Integer.parseInt(dharokBoots.getText()));
					dharokGearChoices.add(Integer.parseInt(dharokNecklace.getText()));
					dharokGearChoices.add(Integer.parseInt(dharokCape.getText()));

					veracGearChoices.add(Integer.parseInt(veracWeapon.getText()));
					veracGearChoices.add(Integer.parseInt(veracShield.getText()));
					veracGearChoices.add(Integer.parseInt(veracHelmet.getText()));
					veracGearChoices.add(Integer.parseInt(veracChest.getText()));
					veracGearChoices.add(Integer.parseInt(veracLegs.getText()));
					veracGearChoices.add(Integer.parseInt(veracGloves.getText()));
					veracGearChoices.add(Integer.parseInt(veracBoots.getText()));
					veracGearChoices.add(Integer.parseInt(veracNecklace.getText()));
					veracGearChoices.add(Integer.parseInt(veracCape.getText()));
					

					toragGearChoices.add(Integer.parseInt(toragWeapon.getText()));
					toragGearChoices.add(Integer.parseInt(toragShield.getText()));
					toragGearChoices.add(Integer.parseInt(toragHelmet.getText()));
					toragGearChoices.add(Integer.parseInt(toragChest.getText()));
					toragGearChoices.add(Integer.parseInt(toragLegs.getText()));
					toragGearChoices.add(Integer.parseInt(toragGloves.getText()));
					toragGearChoices.add(Integer.parseInt(toragBoots.getText()));
					toragGearChoices.add(Integer.parseInt(toragNecklace.getText()));
					toragGearChoices.add(Integer.parseInt(toragCape.getText()));
					
					karilGearChoices.add(Integer.parseInt(karilWeapon.getText()));
					karilGearChoices.add(Integer.parseInt(karilShield.getText()));
					karilGearChoices.add(Integer.parseInt(karilHelmet.getText()));
					karilGearChoices.add(Integer.parseInt(karilChest.getText()));
					karilGearChoices.add(Integer.parseInt(karilLegs.getText()));
					karilGearChoices.add(Integer.parseInt(karilGloves.getText()));
					karilGearChoices.add(Integer.parseInt(karilBoots.getText()));
					karilGearChoices.add(Integer.parseInt(karilNecklace.getText()));
					karilGearChoices.add(Integer.parseInt(karilCape.getText()));
					
					guthanGearChoices.add(Integer.parseInt(guthanWeapon.getText()));
					guthanGearChoices.add(Integer.parseInt(guthanShield.getText()));
					guthanGearChoices.add(Integer.parseInt(guthanHelmet.getText()));
					guthanGearChoices.add(Integer.parseInt(guthanChest.getText()));
					guthanGearChoices.add(Integer.parseInt(guthanLegs.getText()));
					guthanGearChoices.add(Integer.parseInt(guthanGloves.getText()));
					guthanGearChoices.add(Integer.parseInt(guthanBoots.getText()));
					guthanGearChoices.add(Integer.parseInt(guthanNecklace.getText()));
					guthanGearChoices.add(Integer.parseInt(guthanCape.getText()));
		
					ahrimGearChoices.add(Integer.parseInt(ahrimWeapon.getText()));
					ahrimGearChoices.add(Integer.parseInt(ahrimShield.getText()));
					ahrimGearChoices.add(Integer.parseInt(ahrimHelmet.getText()));
					ahrimGearChoices.add(Integer.parseInt(ahrimChest.getText()));
					ahrimGearChoices.add(Integer.parseInt(ahrimLegs.getText()));
					ahrimGearChoices.add(Integer.parseInt(ahrimGloves.getText()));
					ahrimGearChoices.add(Integer.parseInt(ahrimBoots.getText()));
					ahrimGearChoices.add(Integer.parseInt(ahrimNecklace.getText()));
					ahrimGearChoices.add(Integer.parseInt(ahrimCape.getText()));

					addChoices(dharokGearChoices, Brother.DHAROK);
					addChoices(veracGearChoices, Brother.VERAC);
					addChoices(toragGearChoices, Brother.TORAG);
					addChoices(karilGearChoices, Brother.KARIL);
					addChoices(guthanGearChoices, Brother.GUTHAN);
					addChoices(ahrimGearChoices, Brother.AHRIM);
					
					

					if(!dharokAutocast.getSelectedItem().equals("None"))
						Brother.DHAROK.setAutocast(getSpell(dharokAutocast.getSelectedItem()));
					if(!veracAutocast.getSelectedItem().equals("None"))
						Brother.VERAC.setAutocast(getSpell(veracAutocast.getSelectedItem()));
					if(!toragAutocast.getSelectedItem().equals("None"))
						Brother.TORAG.setAutocast(getSpell(toragAutocast.getSelectedItem()));
					if(!karilAutocast.getSelectedItem().equals("None"))
						Brother.KARIL.setAutocast(getSpell(karilAutocast.getSelectedItem()));
					if(!guthanAutocast.getSelectedItem().equals("None"))
						Brother.GUTHAN.setAutocast(getSpell(guthanAutocast.getSelectedItem()));
					if(!ahrimAutocast.getSelectedItem().equals("None"))
						Brother.AHRIM.setAutocast(getSpell(ahrimAutocast.getSelectedItem()));
					addRunes();
					
					
					if(toBankComboBox.getSelectedItem().equals("House portal")){
						toBankChoice = ToBankChoices.HOUSE_PORTAL;
						houseState = HouseTraverse.TO_BANK;
						prepInventory.add(new Item(HouseTraverse.HOUSE_TAB_ID,1));
					}
					if(toBankComboBox.getSelectedItem().equals("Ectofungus")){
						toBankChoice = ToBankChoices.ECTO;
						ectoState = EctoTraverse.TO_BANK;
						prepInventory.add(new Item(EctoTraverse.ECTOPHIAL_ID,1));
					}
					if(toBankComboBox.getSelectedItem().equals("Ancients Teleport")){
						toBankChoice = ToBankChoices.ANCIENTS_TELEPORT;
						ancientsState = AncientsTraverse.TO_BANK;
						prepInventory.add(new Item(AttackStyles.Rune.BLOOD.getId(),1));
						prepInventory.add(new Item(AttackStyles.Rune.LAW.getId(),2));
					}
					if(fromBankComboBox.getSelectedItem().equals("Cellar shortcut")){
						fromBankChoice = FromBankChoices.BAR_CELLAR;
					}
					if(fromBankComboBox.getSelectedItem().equals("Swamp")){
						fromBankChoice = FromBankChoices.SWAMP;
					}
					try{
						foodId = Integer.parseInt(foodIdField.getText());
					}catch(NumberFormatException e3){
						log.info("invalid food id input");
						error = "Invalid food id";
						setVisible(false);
						dispose();
					}
					try{
						numFood = Integer.parseInt(foodQtyField.getText());
					}catch(NumberFormatException e3){
						log.info("invalid num food input");
						error = "Invalid # food";
						setVisible(false);
						dispose();
					}
					try{
						numPrayPotion = Integer.parseInt(prayQtyField.getText());
					}catch(NumberFormatException e3){
						log.info("invalid num pray potion input");
						error = "Invalid # pray potion";
						setVisible(false);
						dispose();
					}


					dharokProtPray = dharokProtectionPrayButton.isSelected();
					veracProtPray = veracProtectionPrayButton.isSelected();
					toragProtPray = toragProtectionPrayButton.isSelected();
					karilProtPray = karilProtectionPrayButton.isSelected();
					guthanProtPray = guthanProtectionPrayButton.isSelected();
					ahrimProtPray = ahrimProtectionPrayButton.isSelected();

					start = true;
					setVisible(false);
				    dispose();
				}
			}
			private void addChoices(ArrayList<Integer> gear, Brother b){
				for(int id : gear){
					if(id != 0 && id != -1)
						b.addGearIds(id);
				}
			}
			
			private AttackStyles.StandardSpells getSpell(Object o){
				if(o.equals("Magic Dart")) return AttackStyles.StandardSpells.MAGIC_DART;
				if(o.equals("Fire Surge")) return AttackStyles.StandardSpells.FIRE_SURGE;
				return null;
			}
			
			private void addRunes(){
				AttackStyles.StandardSpells spell = null;
					for(Brother b:Brother.values())
						if(b.isAutocasting())
							spell = b.getSpell();
				if(spell == null) return;
				if(spell == AttackStyles.StandardSpells.FIRE_SURGE){
					prepInventory.add(new Item(AttackStyles.Rune.FIRE.getId(),5000));
					prepInventory.add(new Item(AttackStyles.Rune.AIR.getId(),3500));
					prepInventory.add(new Item(AttackStyles.Rune.DEATH.getId(),500));
					prepInventory.add(new Item(AttackStyles.Rune.BLOOD.getId(),500));
				}
				if(spell == AttackStyles.StandardSpells.MAGIC_DART){
					prepInventory.add(new Item(AttackStyles.Rune.DEATH.getId(),500));
					prepInventory.add(new Item(AttackStyles.Rune.MIND.getId(),2000));
				}
			}
		}
	}
}
