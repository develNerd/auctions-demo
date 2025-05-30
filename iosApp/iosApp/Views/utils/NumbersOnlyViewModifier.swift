//
// Created for NumericTextFields
// by Stewart Lynch on 2022-12-18
// Using Swift 5.0

import SwiftUI
import Combine

// A custom view modifier for filtering inputs when placing a bid.
//It supporst both decimals and integers. However now it's being used with integer
struct NumbersOnlyViewModifier: ViewModifier {

    @Binding var text: String
    var includeDecimal: Bool
    var isCurrency: Bool // New property to indicate currency formatting

    func body(content: Content) -> some View {
        content
            .keyboardType(includeDecimal || isCurrency ? .decimalPad : .numberPad) // Use decimalPad for currency too
            .onReceive(Just(text)) { newValue in
                if isCurrency {
                    // Remove existing grouping separators and non-numeric characters for processing
                    let cleanString = newValue.filter { "0123456789".contains($0) }

                    // Convert to a number to format it
                    if let number = Int(cleanString) { // Use Int as we're not allowing decimals for currency here
                        let formatter = NumberFormatter()
                        formatter.numberStyle = .decimal // Use decimal style for grouping separators
                        formatter.usesGroupingSeparator = true
                        formatter.groupingSeparator = Locale.current.groupingSeparator ?? "," // Get localized grouping separator

                        if let formattedString = formatter.string(from: NSNumber(value: number)) {
                            if self.text != formattedString {
                                self.text = formattedString
                            }
                        }
                    } else if cleanString.isEmpty && !newValue.isEmpty {
                        // Allows clearing the text field
                        self.text = ""
                    }
                } else {
                    var numbers = "0123456789"
                    let decimalSeparator: String = Locale.current.decimalSeparator ?? "."
                    if includeDecimal {
                        numbers += decimalSeparator
                    }
                    if newValue.components(separatedBy: decimalSeparator).count - 1 > 1 {
                        let filtered = newValue
                        self.text = String(filtered.dropLast())
                    } else {
                        let filtered = newValue.filter { numbers.contains($0) }
                        if filtered != newValue {
                            self.text = filtered
                        }
                    }
                }
            }
    }
}

extension View {
    func numbersOnly(_ text: Binding<String>, includeDecimal: Bool = false, isCurrency: Bool = true) -> some View {
        self.modifier(NumbersOnlyViewModifier(text: text, includeDecimal: includeDecimal, isCurrency: isCurrency))
    }
}

extension String {
    func toDoubleFromFormattedString() -> Double {
        let groupingSeparator: String = Locale.current.groupingSeparator ?? ","
        let cleanedString = self.replacingOccurrences(of: groupingSeparator, with: "")
        return Double(cleanedString) ?? 0.0
    }
    
    func toDoubleFromLocaleFormattedString() -> Double {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.usesGroupingSeparator = true
        formatter.locale = Locale.current
        
        if let number = formatter.number(from: self) {
            return number.doubleValue
        } else {
            return 0.0
        }
    }
}
