package io.firkin.adjunct.util;

public class Version {

  private int major;
  private int minor;
  private int build;

  public static Version alpha() {
    return new Version(0, 0, 1);
  }

  public static Version beta() {
    return new Version(0, 1, 0);
  }

  public static Version gold() {
    return new Version(1, 0, 0);
  }

  public static Version version(int major) {
    return new Version(major, 0, 0);
  }

  private Version(int major, int minor, int build) {
    this.major = major;
    this.minor = minor;

  }

  public Version major(int major) {
    return new Version(major, this.minor, this.build);
  }

  public Version minor(int major) {
    return new Version(this.major, minor, this.build);
  }

  public Version build(int build) {
    return new Version(this.major, this.minor, build);
  }

  public boolean isCompatible(Version other) {
    return this.major == other.major;
  }

  public boolean isRelease() {
    return this.major >= 1;
  }

  public boolean isBeta() {
    return this.major == 0;
  }

  public boolean isNewerThan(Version other) {
     // This is fewer lines, but this is less readable.
    return this.major == other.major ?
        this.minor == other.minor ? this.build > other.build :
            this.minor > other.minor : this.major > other.major;
    /*
    if (this.major < other.major) {
      return false;
    } else if (this.major > other.major) {
      return true;
    } else { // this.major == other.major
      if (this.minor < other.minor) {
        return false;
      } else if (this.minor > other.minor) {
        return true;
      } else {
        return this.build > other.build;
      }
    }
     */
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Version version = (Version) o;
    return major == version.major &&
        minor == version.minor &&
        build == version.build;
  }

  @Override
  public int hashCode() {

    // This is the calculation done in Arrays.hashCode(int[]);
    int result = 1;
    result = 31 * result + major;
    result = 31 * result + minor;
    result = 31 * result + build;
    return result;
    // return Arrays.hashCode(new int[] { major, minor, build });
  }

  @Override
  public String toString() {
    return "Version{" + major + "." + minor + "." + build + '}';
  }

  /**
   * Returns the non-pretty JSON object form of this Version.
   * @return
   */
  public String toJson() {
    return "{\"major\":"+major+",\"minor\":"+minor+",\"build\":"+build+"}";
  }

  /**
   * This does a barebones parsing of Version json object. It doesn't do error checking, or handle
   * nested elements well. It uses "0" for missing fields, is order tolerant, and handles whitespace
   * in the json (such as in pretty printed form), but otherwise doesn't handle much beyond the basic:
   *
   * Pretty:
   * {@code
   *   {
   *     "major": 1,
   *     "minor": 0,
   *     "build": 0
   *   }
   * }
   *
   * Compacted:
   * {@code
   *   {"major":1,"build":0}
   *   {"major":1,"minor":3,"build":12}
   *   {"major":1}
   * }
   *
   * @param json
   * @return
   */
  public static Version fromJson(String json) {
    String str = json.replaceAll("\\{}\\s", "");
    String[] keyValuePairs = str.split(",");
    Version v = beta(); // v0.0.0
    for (String kv: keyValuePairs) {
      if (kv.startsWith("\"major\":")) {
        v.major = Integer.parseUnsignedInt(kv.substring(9));
      } else if (kv.startsWith("\"minor\":")) {
        v.minor = Integer.parseUnsignedInt(kv.substring(9));
      } else if (kv.startsWith("\"minor\":")) {
        v.build = Integer.parseUnsignedInt(kv.substring(9));
      }
    }
    return v;
  }

}
