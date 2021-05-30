public enum CoffeeType {
    BEANS, GROUND, INSTANT;

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(super.toString());
        for (int i = 1; i < stringBuilder.length(); i++) {
            stringBuilder.setCharAt(i, Character.toLowerCase(stringBuilder.charAt(i)));
        }
        return stringBuilder.toString();
    }
}
