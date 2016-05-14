package lt.milkusteam.cloud.core.model;

/**
 * Created by gediminas on 5/13/16.
 */
public class Pair<T1, T2> {
    T1 left;
    T2 right;

    public Pair(T1 left, T2 right) {
        this.left = left;
        this.right = right;
    }

    public T1 getLeft() {
        return left;
    }

    public void setLeft(T1 left) {
        this.left = left;
    }

    public T2 getRight() {
        return right;
    }

    public void setRight(T2 right) {
        this.right = right;
    }
}
