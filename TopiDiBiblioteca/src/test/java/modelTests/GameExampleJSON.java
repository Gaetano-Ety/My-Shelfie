package modelTests;

import org.json.JSONObject;

public class GameExampleJSON{
	/**
	 * A set of ready-made games useful for extracting test data
	 */
	public static final JSONObject
		ex1 = new JSONObject(
		"{" +
			"\"nPlayers\":4," +
			"\"turn\":0," +
			"\"board\":\"    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /\"," +
			"\"cardsBag\":\"[12, 15, 14, 13, 16, 17]\"," +
			"\"goal1\":{" +
			"\"ID\":9," +
			"\"scores\":[2, 4, 6, 8]" +
			"}," +
			"\"goal2\":{" +
			"\"ID\":12," +
			"\"scores\":[2, 4, 6, 8]" +
			"}," +
			"\"players\":[" +
			"{" +
			"\"nickName\":\"Pippo\"," +
			"\"bookShelf\":\"      /      /      /      /      /\"," +
			"\"score\":0," +
			"\"goal1\":false," +
			"\"goal2\":false," +
			"\"personalGoal\":9" +
			"}," +
			"{" +
			"\"nickName\":\"Giorgio\"," +
			"\"bookShelf\":\"      /      /      /      /      /\"," +
			"\"score\":0," +
			"\"goal1\":false," +
			"\"goal2\":false," +
			"\"personalGoal\":3" +
			"}," +
			"{" +
			"\"nickName\":\"Luca\"," +
			"\"bookShelf\":\"      /      /      /      /      /\"," +
			"\"score\":0," +
			"\"goal1\":false," +
			"\"goal2\":false," +
			"\"personalGoal\":2" +
			"}," +
			"{" +
			"\"nickName\":\"Orlando\"," +
			"\"bookShelf\":\"      /      /      /      /      /\"," +
			"\"score\":0," +
			"\"goal1\":false," +
			"\"goal2\":false," +
			"\"personalGoal\":8" +
			"}" +
			"]" +
			"}"),
		ex2 = new JSONObject(
			"{" +
				"\"nPlayers\":3," +
				"\"turn\":0," +
				"\"board\":\"     G   /    GC   /  CFGGB  /TBPBTBF  / BCTPFPC /  CTGBPCC/  GBPTC  /   FB    /   C     /\"," +
				"\"cardsBag\":\"[12, 15, 14, 13, 16, 17]\"," +
				"\"goal1\":{" +
				"\"ID\":9," +
				"\"scores\":[4]" +
				"}," +
				"\"goal2\":{" +
				"\"ID\":12," +
				"\"scores\":[4, 6]" +
				"}," +
				"\"players\":[" +
				"{" +
				"\"nickName\":\"Giorgio\"," +
				"\"bookShelf\":\"      /      /      /      /      /\"," +
				"\"score\":8," +
				"\"goal1\":true," +
				"\"goal2\":false," +
				"\"personalGoal\":3" +
				"}," +
				"{" +
				"\"nickName\":\"Luca\"," +
				"\"bookShelf\":\"      /      /      /      /      /\"," +
				"\"score\":6," +
				"\"goal1\":true," +
				"\"goal2\":false," +
				"\"personalGoal\":2" +
				"}," +
				"{" +
				"\"nickName\":\"Filippa\"," +
				"\"bookShelf\":\"      /      /      /      /      /\"," +
				"\"score\":8," +
				"\"goal1\":false," +
				"\"goal2\":true," +
				"\"personalGoal\":8" +
				"}" +
				"]" +
				"}"),
		ex3 = new JSONObject(
			"{" +
				"\"nPlayers\":3," +
				"\"turn\":0," +
				"\"board\":\"     G   /    GC   /  CFGGB  /TBPBTBF  / BCTPFPC /  CTGBPCC/  GBPTC  /   FB    /   C     /\"," +
				"\"cardsBag\":\"[12, 15, 14, 13, 16, 17]\"," +
				"\"goal1\":{" +
				"\"ID\":3," +
				"\"scores\":[4, 6, 8]" +
				"}," +
				"\"goal2\":{" +
				"\"ID\":12," +
				"\"scores\":[4, 6, 8]" +
				"}," +
				"\"players\":[" +
				"{" +
				"\"nickName\":\"Giorgio\"," +
				"\"bookShelf\":\"CCCCCC/      /      /      /CCCCCC/\"," +
				"\"score\":0," +
				"\"goal1\":false," +
				"\"goal2\":false," +
				"\"personalGoal\":3" +
				"}," +
				"{" +
				"\"nickName\":\"Luca\"," +
				"\"bookShelf\":\"      /      /      /      /      /\"," +
				"\"score\":0," +
				"\"goal1\":false," +
				"\"goal2\":false," +
				"\"personalGoal\":2" +
				"}," +
				"{" +
				"\"nickName\":\"Filippa\"," +
				"\"bookShelf\":\"    PP/   CCC/  TTTT/ GGGGG/FFFFFF/\"," +
				"\"score\":0," +
				"\"goal1\":false," +
				"\"goal2\":false," +
				"\"personalGoal\":8" +
				"}" +
				"]" +
				"}"),
		ex4 = new JSONObject(
			"{" +
				"\"nPlayers\":2," +
				"\"turn\":1," +
				"\"board\":\"         /    P    /         / C    G  /    C    /         /    C    /   F     /         /\"," +
				"\"cardsBag\":\"[12, 15, 14, 13, 16, 17]\"," +
				"\"goal1\":{" +
				"\"ID\":3," +
				"\"scores\":[4, 8]" +
				"}," +
				"\"goal2\":{" +
				"\"ID\":12," +
				"\"scores\":[4, 8]" +
				"}," +
				"\"players\":[" +
				"{" +
				"\"nickName\":\"Giorgio\"," +
				"\"bookShelf\":\"    PP/   CCC/  TTTT/ GGGGG/FFFFFF/\"," +
				"\"score\":0," +
				"\"goal1\":false," +
				"\"goal2\":false," +
				"\"personalGoal\":3" +
				"}," +
				"{" +
				"\"nickName\":\"Filippa\"," +
				"\"bookShelf\":\"CCCCCC/TTTTTT/TTTTTT/TTTTTT/CCCCCC/\"," +
				"\"score\":0," +
				"\"goal1\":false," +
				"\"goal2\":false," +
				"\"personalGoal\":8" +
				"}" +
				"]" +
				"}"),
		ex5 = new JSONObject(
			"{" +
				"\"nPlayers\":2," +
				"\"turn\":1," +
				"\"board\":\"         /    CC   /   CGP   / BTCTCB  / FGPBCCT /  TGFBBT /   PBB   /   PP    /         /\"," +
				"\"cardsBag\":\"[12, 15, 14, 13, 16, 17]\"," +
				"\"goal1\":{" +
				"\"ID\":3," +
				"\"scores\":[4, 8]" +
				"}," +
				"\"goal2\":{" +
				"\"ID\":12," +
				"\"scores\":[4, 8]" +
				"}," +
				"\"players\":[" +
				"{" +
				"\"nickName\":\"Giorgio\"," +
				"\"bookShelf\":\"    PP/   CCC/  TTTT/ GGGGG/FFFFFF/\"," +
				"\"score\":0," +
				"\"goal1\":false," +
				"\"goal2\":false," +
				"\"personalGoal\":3" +
				"}," +
				"{" +
				"\"nickName\":\"Filippa\"," +
				"\"bookShelf\":\"CCCCCC/      /TTTTTT/TTTTTT/CCCCCC/\"," +
				"\"score\":0," +
				"\"goal1\":false," +
				"\"goal2\":false," +
				"\"personalGoal\":8" +
				"}" +
				"]" +
				"}");
}
