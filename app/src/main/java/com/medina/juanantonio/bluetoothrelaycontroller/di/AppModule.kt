package com.medina.juanantonio.bluetoothrelaycontroller.di

import android.content.Context
import com.medina.juanantonio.bluetoothrelaycontroller.ble.BluetoothLEManager
import com.medina.juanantonio.bluetoothrelaycontroller.ble.BluetoothLEServiceManager
import com.medina.juanantonio.bluetoothrelaycontroller.ble.IBluetoothLEManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideBluetoothLeManager(
        @ApplicationContext context: Context
    ): IBluetoothLEManager {
        return BluetoothLEManager(context)
    }

    @Provides
    @Singleton
    fun provideServiceManager(
        @ApplicationContext context: Context,
        bluetoothLEManager: IBluetoothLEManager
    ): BluetoothLEServiceManager {
        return BluetoothLEServiceManager(context, bluetoothLEManager)
    }
}