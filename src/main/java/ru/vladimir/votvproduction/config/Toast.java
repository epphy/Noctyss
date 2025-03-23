package ru.vladimir.votvproduction.config;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;

public record Toast(boolean enabled, boolean oneTime, ToastNotification toastNotification) {
}
