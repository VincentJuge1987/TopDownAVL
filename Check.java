import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Check {

	private static int OTHER;
	private static final int BEST = 0;
	private static final String TAB = "	";
	private static final int[][] UP = new int[][] { { 1, 1 }, { 1, 2 }, { 2, 1 } };
	private static int line = 0;
	private static final Set<List<Integer>> READ = new HashSet<>();
	private static final Set<List<Integer>> WRITE = new HashSet<>();

	private static List<Integer> stringToList(String s) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ('0' <= c && c <= '9') {
				list.add(c - '0');
			} else if (c == '-' && i + 1 < s.length()) {
				i++;
				c = s.charAt(i);
				if ('1' <= c && c <= '9') {
					list.add('0' - c);
				} else {
					throw new IllegalArgumentException("String " + s + " does not represent an integer list");
				}
			} else {
				throw new IllegalArgumentException("String " + s + " does not represent an integer list");
			}
		}
		return list;
	}

	private static List<Integer> augmented(List<Integer> list, int... p) {
		List<Integer> copy = new ArrayList<>(list);
		for (int q : p) {
			copy.add(q);
		}
		return copy;
	}

	/**
	 * Merges the positions i and i+1 of a given node height list.
	 * 
	 * @param list Input node height list.
	 * @param i    First position to merge.
	 * @return List in which the nodes #i and #i+1 have been merged.
	 */
	private static List<Integer> merge(List<Integer> list, int i) {
		List<Integer> copy = new ArrayList<>();
		for (int j = 0; j < i; j++) {
			copy.add(list.get(j));
		}
		copy.add(Math.min(list.get(i), list.get(i + 1)) - 1);
		for (int j = i + 2; j < list.size(); j++) {
			copy.add(list.get(j));
		}
		return copy;
	}

	/**
	 * Promotes or demotes the position p of a node height list.
	 * 
	 * @param list Input node height list.
	 * @param p    Position to promote or demote.
	 * @param d    Increment to the height of the node.
	 * @return List in which the node p has been promoted of d or demoted of -d.
	 */
	private static List<Integer> delta(List<Integer> list, int p, int d) {
		List<Integer> copy = new ArrayList<>();
		for (int j = 0; j < list.size(); j++) {
			copy.add(list.get(j) + (j == p ? d : 0));
		}
		return copy;
	}

	private static void phase1(String[] tab) {
		String[] data = tab[1].split(":");
		List<Integer> list = stringToList(data[0]);
		int pos = Integer.parseInt(data[1]);
		int split = Integer.parseInt(data[2]);
		int len = pos == split ? 2 : 1;
		if (tab.length != 3 * len + 2 || pos < 0 || pos >= list.size()) {
			throw new IllegalStateException("Error @ line " + line);
		}
		WRITE.add(augmented(list, pos));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < len; j++) {
				match(list, pos, split, i, j, tab[2 + j + len * i].split(":"));
			}
		}
	}

	private static void match(List<Integer> list, int pos, int split, int i, int j, String[] tab) {
		List<Integer> other = IntStream.range(0, tab[0].length()).map(u -> (tab[0].charAt(u) - '0')).boxed().toList();
		int p = Integer.parseInt(tab[1]);
		if (p != pos + (pos > split ? 1 : 0) + j) {
			throw new IllegalStateException("Error @ line " + line);
		}
		for (int u = 0; u < list.size(); u++) {
			if ((u < split && list.get(u) != other.get(u)) || (u > split && list.get(u) != other.get(u + 1))
					|| (u == split && list.get(u) + UP[i][0] != other.get(u))
					|| (u == split && list.get(u) + UP[i][1] != other.get(u + 1))) {
				throw new IllegalStateException("Error @ line " + line);
			}
		}
		READ.add(augmented(other, p));
	}

	private static void phase2(String[] tab) {
		if (tab.length != 4) {
			throw new IllegalStateException("Error @ line " + line);
		}
		String[] data1 = tab[1].split(":");
		List<Integer> list1 = stringToList(data1[0]);
		int pos = Integer.parseInt(data1[1]);
		int target1 = Integer.parseInt(data1[2]);
		String[] data2 = tab[2].split(":");
		List<Integer> list2 = stringToList(data2[0]);
		int target2 = Integer.parseInt(data2[1]);
		String[] data3 = tab[3].split(":");
		List<Integer> list3 = stringToList(data3[0]);
		int target3 = Integer.parseInt(data3[1]);
		WRITE.add(augmented(list1, pos));
		if (pos < 0 || pos >= list1.size() || (target1 != BEST && target1 != OTHER) || target1 != target2
				|| target2 != target3 || !list2.equals(list1) || !list3.equals(delta(list1, pos, OTHER))) {
			throw new IllegalStateException("Error @ line " + line + ":" + pos + ":" + list1 + "/" + list2 + "/" + list3
					+ " : " + target1 + ":" + target2 + ":" + target3 + "/" + delta(list1, pos, OTHER));
		}
		READ.add(augmented(list2, target2, Integer.MAX_VALUE));
		READ.add(augmented(list3, target3, Integer.MAX_VALUE));
	}

	private static void phase3(String[] tab) {
		if (tab.length != 3) {
			throw new IllegalStateException("Error @ line " + line);
		}
		String[] data1 = tab[1].split(":");
		List<Integer> list1 = stringToList(data1[0]);
		int target1 = Integer.parseInt(data1[1]);
		int m = Integer.parseInt(data1[2]);
		String[] data2 = tab[2].split(":");
		List<Integer> list2 = stringToList(data2[0]);
		int target2 = Integer.parseInt(data2[1]);
		WRITE.add(augmented(list1, target1, Integer.MAX_VALUE));
		if (m < 0 || m >= list1.size() - 1 || target1 != target2 || (target1 != BEST && target1 != OTHER)
				|| !list2.equals(merge(list1, m))) {
			throw new IllegalStateException("Error @ line " + line);
		}
		READ.add(augmented(list2, target2, Integer.MAX_VALUE));
	}

	private static void check(String s) {
		String[] tab = s.split(TAB);
		if (tab[0].equals("E")) {
			phase1(tab);
		} else if (tab[0].equals("T")) {
			phase2(tab);
		} else if (tab[0].equals("M")) {
			phase3(tab);
		} else {
			throw new IllegalStateException("Error @ line " + line);
		}
	}

	private static void read() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean b = true;
		while (b) {
			String s = br.readLine();
			b = s != null;
			if (b) {
				line++;
				check(s);
			}
		}
		br.close();
	}

	public static void main(String[] args) throws IOException {
		String goal = null;
		switch (args[0]) {
		case "insertion":
		case "i":
			OTHER = -1;
			goal = "insertion";
			break;
		case "deletion":
		case "d":
			OTHER = 1;
			goal = "deletion";
			break;
		default:
			throw new IllegalStateException(
					"The command should be of the form: java Check [insertion/i/deletion/d] < input-file");
		}
		READ.add(List.of(BEST, BEST));
		WRITE.add(List.of(BEST, BEST, Integer.MAX_VALUE));
		WRITE.add(List.of(OTHER, OTHER, Integer.MAX_VALUE));
		read();
		if (READ.equals(WRITE)) {
			System.out.println("Input strategy for " + goal + " is valid!");
		} else {
			System.out.println("Input strategy for " + goal + " is invalid!");
		}
	}
}
