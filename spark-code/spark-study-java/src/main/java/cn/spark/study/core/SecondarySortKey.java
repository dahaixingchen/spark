package cn.spark.study.core;

import java.io.Serializable;

import scala.math.Ordered;

/**
 * 自定义的二次排序key
 *
 * @author Administrator
 */
public class SecondarySortKey implements Ordered<SecondarySortKey>, Serializable {

    private static final long serialVersionUID = -2366006422945129991L;

    // 首先在自定义key里面，定义需要进行排序的列
    private int first;
    private int second;

    @Override
    public String toString() {
        return "SecondarySortKey{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    public SecondarySortKey(int first, int second) {
        this.first = first;
        this.second = second;
    }

    // 为要进行排序的多个列，提供getter和setter方法，以及hashcode和equals方法
    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public boolean $greater(SecondarySortKey other) {
        if (this.first > other.getFirst()) {
            return true;
        } else if (this.first == other.getFirst() &&
                this.second > other.getSecond()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $greater$eq(SecondarySortKey other) {
        if (this.$greater(other)) {
            return true;
        } else if (this.first == other.getFirst() &&
                this.second == other.getSecond()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $less(SecondarySortKey other) {
        if (this.first < other.getFirst()) {
            return true;
        } else if (this.first == other.getFirst() &&
                this.second < other.getSecond()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $less$eq(SecondarySortKey other) {
        if (this.$less(other)) {
            return true;
        } else if (this.first == other.getFirst() &&
                this.second == other.getSecond()) {
            return true;
        }
        return false;
    }

    @Override
    public int compare(SecondarySortKey other) {
        if (this.first - other.getFirst() != 0) {
            return this.first - other.getFirst();
        } else {
            return this.second - other.getSecond();
        }
    }

    @Override
    public int compareTo(SecondarySortKey other) {
        if (this.first - other.getFirst() != 0) {
            return this.first - other.getFirst();
        } else {
            return this.second - other.getSecond();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + first;
        result = prime * result + second;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SecondarySortKey other = (SecondarySortKey) obj;
        if (first != other.first)
            return false;
        if (second != other.second)
            return false;
        return true;
    }

}
