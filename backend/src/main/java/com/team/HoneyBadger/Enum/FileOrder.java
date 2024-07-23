package com.team.HoneyBadger.Enum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;

public enum FileOrder {
    TYPE {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                if (f1.isDirectory() && f2.isDirectory()) return 0;
                else if (f1.isDirectory()) return -1;
                else if (f2.isDirectory()) return 1;
                else {
                    FileType type1 = FileType.get(f1);
                    FileType type2 = FileType.get(f2);
                    return type1.compareTo(type2);
                }
            };
        }
    }, NAME_ASC {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                if (f1.isDirectory() && f2.isDirectory()) return f1.getName().compareTo(f2.getName());
                else if (f1.isDirectory()) return -1;
                else if (f2.isDirectory()) return 1;
                else return f1.getName().compareTo(f2.getName());
            };
        }
    }, NAME_DESC {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                if (f1.isDirectory() && f2.isDirectory()) return f1.getName().compareTo(f2.getName()) * -1;
                else if (f1.isDirectory()) return -1;
                else if (f2.isDirectory()) return 1;
                else return f1.getName().compareTo(f2.getName()) * -1;
            };
        }
    }, SIZE_ASC {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                if (f1.isDirectory() && f2.isDirectory()) {
                    long size1 = getSize(f1);
                    long size2 = getSize(f2);
                    return Long.compare(size1, size2);
                } else if (f1.isDirectory()) return -1;
                else if (f2.isDirectory()) return 1;
                else return Long.compare(f1.length(), f2.length());
            };
        }
    }, SIZE_DESC {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                if (f1.isDirectory() && f2.isDirectory()) {
                    long size1 = getSize(f1);
                    long size2 = getSize(f2);
                    return Long.compare(size1, size2) * -1;
                } else if (f1.isDirectory()) return -1;
                else if (f2.isDirectory()) return 1;
                else return Long.compare(f1.length(), f2.length()) * -1;
            };
        }
    }, MODIFY_ASC {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                if (f1.isDirectory() && f2.isDirectory()) {
                    return Long.compare(f1.lastModified(), f2.lastModified());
                } else if (f1.isDirectory()) return -1;
                else if (f2.isDirectory()) return 1;
                else return Long.compare(f1.lastModified(), f2.lastModified()) ;
            };
        }
    }, MODIFY_DESC {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                if (f1.isDirectory() && f2.isDirectory()) {
                    return Long.compare(f1.lastModified(), f2.lastModified())* -1;
                } else if (f1.isDirectory()) return -1;
                else if (f2.isDirectory()) return 1;
                else return Long.compare(f1.lastModified(), f2.lastModified())* -1;
            };
        }
    }, CREATE_ASC {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                try {
                    if (f1.isDirectory() && f2.isDirectory()) {
                        long create1 = ((FileTime) Files.getAttribute(f1.toPath(), "creationTime")).toMillis();
                        long create2 = ((FileTime) Files.getAttribute(f2.toPath(), "creationTime")).toMillis();
                        return Long.compare(create1, create2);
                    } else if (f1.isDirectory()) return -1;
                    else if (f2.isDirectory()) return 1;
                    else {
                        long create1 = ((FileTime) Files.getAttribute(f1.toPath(), "creationTime")).toMillis();
                        long create2 = ((FileTime) Files.getAttribute(f2.toPath(), "creationTime")).toMillis();
                        return Long.compare(create1, create2);
                    }
                } catch (IOException ignored) {
                }
                return 0;
            };
        }
    },

    CREATE_DESC {
        @Override
        public Comparator<File> getComparator() {
            return (f1, f2) -> {
                try {
                    if (f1.isDirectory() && f2.isDirectory()) {
                        long create1 = ((FileTime) Files.getAttribute(f1.toPath(), "creationTime")).toMillis();
                        long create2 = ((FileTime) Files.getAttribute(f2.toPath(), "creationTime")).toMillis();
                        return Long.compare(create1, create2)* -1;
                    } else if (f1.isDirectory()) return -1;
                    else if (f2.isDirectory()) return 1;
                    else {
                        long create1 = ((FileTime) Files.getAttribute(f1.toPath(), "creationTime")).toMillis();
                        long create2 = ((FileTime) Files.getAttribute(f2.toPath(), "creationTime")).toMillis();
                        return Long.compare(create1, create2)* -1;
                    }
                } catch (IOException ignored) {
                }
                return 0;
            };
        }
    }
    //
    ;

    public abstract Comparator<File> getComparator();

    public static long getSize(File file) {
        long size = 0;
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File list : file.listFiles())
                    size += getSize(list);
            }
            size += file.length();
        }
        return size;
    }
}
