package ru.vladimir.noctyss.config.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;

public record Toast(boolean oneTime, ToastNotification toastNotification) {
}
