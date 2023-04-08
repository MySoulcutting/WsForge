package cn.whitesoul.wsforge.data;



import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    public static ConcurrentHashMap<UUID, Integer> level = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Integer> exp = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, String> names = new ConcurrentHashMap<>();

}

