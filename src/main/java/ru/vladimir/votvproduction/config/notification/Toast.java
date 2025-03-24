package ru.vladimir.votvproduction.config.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;

public record Toast(boolean enabled, boolean oneTime, ToastNotification toastNotification) {
}
