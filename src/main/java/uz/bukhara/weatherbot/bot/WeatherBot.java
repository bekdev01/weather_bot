package uz.bukhara.weatherbot.bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.bukhara.weatherbot.bot.modelWeather.ListItem;
import uz.bukhara.weatherbot.bot.modelWeather.ResponseWeather;
import uz.bukhara.weatherbot.bot.modelWeather.WeatherItem;
import uz.bukhara.weatherbot.bot.serive.ServiceWeather;
import uz.bukhara.weatherbot.bot.utills.Constants;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Component
public class WeatherBot extends TelegramLongPollingBot {

    @Value("${botToken}")
     String botToken;

    @Value("${botUsername}")
     String botUsername;


    boolean changeCityName = false;


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals(Constants.START) || text.equals(Constants.BACK)) {
                    execute(mainMenu(update));
                    changeCityName = false;
                } else if (text.equals(Constants.SET_CITY_NAME)) {
                    KeyboardRow row = new KeyboardRow();
                    row.add(Constants.BACK);
                    execute(new SendMessage(chatId, Constants.SET_CITY_NAME_TEXT)
                            .setReplyMarkup(new ReplyKeyboardMarkup()
                                    .setResizeKeyboard(true)
                                    .setSelective(false)
                                    .setKeyboard(new ArrayList<>(Collections.singletonList(row)))));
                    changeCityName = true;
                } else if (changeCityName) {
                    Set<SendMessage> sendMessageSet = sendWeather(update);
                    for (SendMessage sendMessage : sendMessageSet) {
                        execute(sendMessage);
                        break;
                    }
                } else {
                    execute(new SendMessage(chatId, Constants.MAIN_MENU_TEXT)
                            .setReplyToMessageId(message.getMessageId()));
                }
            } else if (message.hasLocation()) {
                Set<SendMessage> sendMessageSet = sendWeather(update);
                for (SendMessage sendMessage : sendMessageSet) {
                    execute(sendMessage);
                    break;
                }
                changeCityName = false;
            }
        }

    }


    private SendMessage mainMenu(Update update) {
        Message message = update.getMessage();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        replyKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage()
                .setChatId(message.getChatId())
                .setText(Constants.MAIN_MENU_TEXT)
                .setReplyMarkup(replyKeyboardMarkup)
                .setParseMode(ParseMode.MARKDOWN);

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.SET_CITY_NAME));
        row.add(new KeyboardButton(Constants.SET_LOCATION).setRequestLocation(true));
        rowList.add(row);

        return sendMessage;
    }


    private Set<SendMessage> sendWeather(Update update) throws IOException {
        Set<SendMessage> sendMessageSet = new HashSet<>();
        Message message = update.getMessage();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        replyKeyboardMarkup.setKeyboard(rowList);

        StringBuilder builder = new StringBuilder();
        ResponseWeather weather;

        SendMessage sendMessage = new SendMessage()
                .setChatId(message.getChatId())
                .setReplyToMessageId(message.getMessageId())
                .setParseMode(ParseMode.MARKDOWN)
                .setReplyMarkup(replyKeyboardMarkup);


        if (message.hasLocation()) {
            Float latitude = message.getLocation().getLatitude();
            Float longitude = message.getLocation().getLongitude();
            weather = ServiceWeather.getWeatherFromLocation(latitude, longitude);
        } else {
            weather = ServiceWeather.getWeatherFromCity(message.getText());
            KeyboardRow row = new KeyboardRow();
            row.add(Constants.BACK);
            rowList.add(row);
        }

        if (weather.getList() != null && !weather.getList().isEmpty()) {

            for (ListItem item : weather.getList()) {
                String country = item.getSys().getCountry();
                String city = item.getName();
                for (WeatherItem weatherItem : item.getWeather()) {
                    String state = weatherItem.getDescription();
                    String rain = item.getRain() == null ? null : item.getRain().toString();
                    String snow = item.getSnow() == null ? null : item.getSnow().toString();
                    String tempMax = String.valueOf(item.getMain().getTempMax() - 273.15).split("\\.")[0];
                    String tempMin = String.valueOf(item.getMain().getTempMin() - 273.15).split("\\.")[0];
                    String tempNow = String.valueOf(item.getMain().getTemp() - 273.15).split("\\.")[0];

                    double pressure = (double) item.getMain().getPressure() / 10;
                    int humidity = item.getMain().getHumidity();
                    int allClouds = item.getClouds().getAll();
                    double windSpeed = item.getWind().getSpeed();
                    double lat = item.getCoord().getLat();
                    double lon = item.getCoord().getLon();

                    builder.append(country.equals("UZ")?"\uD83C\uDDF8\uD83C\uDDF1":"\uD83C\uDFF3 ").append(country)
                            .append("\n")
                            .append("\uD83C\uDF06 City: ").append(city)
                            .append("\n")
                            .append("\uD83C\uDF24 Weather: ").append(state)
                            .append("\n")
                            .append("☁️ Clouds: ").append(allClouds).append(" %")
                            .append("\n")
                            .append("☔️ Rain: ").append(rain == null ? "no" : rain.split("=")[1].substring(0,rain.split("=")[1].length()-1)).append(rain==null?"":" mm in an hour")
                            .append("\n")
                            .append("❄️ Snow: ").append(snow == null ? "no" : snow)
                            .append("\n")
                            .append("\uD83C\uDF21 Temperature: ").append(tempNow).append(" C")
                            .append("\n")
                            .append("\uD83C\uDF21 Max temperature: ").append(tempMax).append(" C")
                            .append("\n")
                            .append("\uD83C\uDF21 Min temperature: ").append(tempMin).append(" C")
                            .append("\n")
                            .append("\uD83C\uDF9A Pressure: ").append(pressure).append(" kPa")
                            .append("\n")
                            .append("\uD83D\uDCA7 Humidity: ").append(humidity).append(" %")
                            .append("\n")
                            .append("\uD83D\uDCA8 Wind speed: ").append(windSpeed).append(" m/s")
                            .append("\n")
                            .append("\uD83D\uDDFA Lat: ").append(lat)
                            .append("\n")
                            .append("\uD83D\uDDFA Lon: ").append(lon)
                            .append("\n")
                            .append("\uD83D\uDD50 Time: ").append(LocalDate.now())
                            .append("\n\n")
                            .append("-------------------------------\n\n");
                    sendMessage.setText(builder.toString());
                    sendMessageSet.add(sendMessage);
                }

            }

        } else {
            sendMessage.setText(Constants.NOT_FOUND);
            sendMessageSet.add(sendMessage);
        }

        return sendMessageSet;

    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}
