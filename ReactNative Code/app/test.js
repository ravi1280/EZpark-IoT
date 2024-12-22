
import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';

export default function DistanceApp  () {
    const [distance, setDistance] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const ws = new WebSocket('ws://c9d5-112-134-231-66.ngrok-free.app/EZPark/distance');

        ws.onopen = () => {
            console.log('WebSocket connection opened');
        };

        ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                setDistance(data.distance);
            } catch (err) {
                console.error('Error parsing message:', err);
            }
        };

        ws.onerror = (e) => {
            setError(e.message);
            console.error('WebSocket error:', e.message);
        };

        ws.onclose = () => {
            console.log('WebSocket connection closed');
        };

        return () => {
            ws.close();
        };
    }, []);

    return (
        <View style={styles.container}>
            <Text style={styles.text}>
                {distance !== null
                    ? `Latest Distance: ${distance} cm`
                    : 'Waiting for distance...'}
            </Text>
            {error && <Text style={styles.error}>Error: {error}</Text>}
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    text: {
        fontSize: 24,
    },
    error: {
        color: 'red',
        marginTop: 20,
    },
});


