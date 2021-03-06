package opentree.constants;

/**
 * A general-pripose container for information that does not change but does not necessarily fit into other enums.
 * May need to be refactored at some point (into more specific enums) if it gets unwieldy. This enum uses a generic
 * structure to contain any type of information. The data objects themselves are stored in the `value` parameter,
 * and the type is indicated by the type stored in `type`.
 * 
 * @author cody
 *
 */
public enum GeneralConstants {

	DRAFT_TREE_NAME (String.class, "otol.draft.22");

    public final Class<?> type;
    public final Object value;

	GeneralConstants(Class<?> type, Object value) {
		this.type = type;
		this.value = value;
	}
}
