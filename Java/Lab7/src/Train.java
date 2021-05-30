import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Objects;

public class Train implements Serializable {
    private Integer number;
    private String from;
    private String to;
    private LocalTime departure;
    private LocalTime arrival;
    private Locale locale;
    private Cost cost;

    public Train(Integer number, String from, String to, LocalTime departure, LocalTime arrival, Double value, Locale locale) {
        this.number = number;
        this.from = from;
        this.to = to;
        this.departure = departure;
        this.arrival = arrival;
        this.locale = locale;
        cost = new Cost(value, locale);
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public LocalTime getDeparture() {
        return departure;
    }

    public void setDeparture(LocalTime departure) {
        this.departure = departure;
    }

    public LocalTime getArrival() {
        return arrival;
    }

    public void setArrival(LocalTime arrival) {
        this.arrival = arrival;
    }

    public String getCost() {
        return cost.toString();
    }

    public void setCost(String cost) throws ParseException {
        this.cost.setValue(cost);
    }

    public void setCost(Double cost) {
        this.cost.setValue(cost);
    }


    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(number).append(",'");
        sb.append(from).append("','");
        sb.append(to).append("',");
        sb.append(departure.getHour()).append(',').append(departure.getMinute()).append(',');
        sb.append(arrival.getHour()).append(',').append(arrival.getMinute()).append(',');
        sb.append(cost.value).append(",'").append(locale.toLanguageTag()).append("'");
        return sb.toString();
    }

    public static class Cost implements Comparable<Cost>, Serializable {
        private Double value;
        private NumberFormat formatter;

        private Cost(Double value, Locale locale) {
            this.value = value;
            formatter = NumberFormat.getCurrencyInstance(locale);
        }

        void setValue(String string) throws ParseException {
            value = formatter.parse(string).doubleValue();
        }

        void setValue(Double value) {
            this.value = value;
        }

        @Override
        public int compareTo(Cost o) {
            return value.compareTo(o.value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cost)) return false;
            Cost cost = (Cost) o;
            return value.equals(cost.value) &&
                    formatter.equals(cost.formatter);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, formatter);
        }

        @Override
        public String toString() {
            return formatter.format(value);
        }
    }
}

