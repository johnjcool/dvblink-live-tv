package io.github.johnjcool.dvblink.live.tv.di;

import io.github.johnjcool.dvblink.live.tv.Application;

public class Injector {

      public static SingletonComponent get() {
          return Application.get().getSingletonComponent();
      }
}